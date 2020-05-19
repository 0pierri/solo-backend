package com.fdmgroup.backend.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.backend.exception.NotFoundException;
import com.fdmgroup.backend.model.*;
import com.fdmgroup.backend.service.ListService;
import com.fdmgroup.backend.service.TaskService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class ListControllerTest {

    @MockBean
    ListService mockListService;
    @MockBean
    TaskService mockTaskService;

    @InjectMocks
    ListController controller;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;

    @BeforeEach
    void initMock() {
        MockitoAnnotations.initMocks(this);
        controller = new ListController(mockListService, mockTaskService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @WithUserDetails("user")
    void when_getLists_thenReturnServiceResults() throws Exception {
        TaskListDTO resultDto = new TaskListDTO();
        List<TaskListDTO> dtoList = List.of(resultDto);
        given(mockListService.findAllByOwnerId(anyLong())).willReturn(dtoList);

        mvc.perform(get("/lists"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(dtoList)));
        verify(mockListService).findAllByOwnerId(1L);
    }

    @Test
    @WithUserDetails("user")
    void when_getSharedLists_thenReturnServiceResults() throws Exception {
        TaskListDTO resultDto = new TaskListDTO();
        List<TaskListDTO> dtoList = List.of(resultDto);
        given(mockListService.findAllByViewerId(anyLong())).willReturn(dtoList);

        mvc.perform(get("/lists/shared"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(dtoList)));
        verify(mockListService).findAllByViewerId(1L);
    }

    @Test
    @WithUserDetails("user")
    void when_getListsById_thenReturnServiceResults() throws Exception {
        TaskListDTO resultDto = new TaskListDTO();
        given(mockListService.findById(anyLong())).willReturn(Optional.of(resultDto));

        mvc.perform(get("/lists/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(resultDto)));
        verify(mockListService).findById(1L);
    }

    @Test
    @WithUserDetails("user")
    void when_createList_thenCreateListWithCurrentUser() throws Exception {
        // User must have ID 1 in DataLoader or the DTOs won't match
        TaskListDTO dto = new TaskListDTO();
        dto.setOwnerId(1L);
        TaskListDTO resultDTO = new TaskListDTO();
        given(mockListService.create(isA(TaskListDTO.class))).willReturn(resultDTO);

        mvc.perform(post("/lists"))
                .andExpect(status().isCreated())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(resultDTO)));
        verify(mockListService).create(dto);
    }

    @Test
    @WithUserDetails("user")
    void when_updateList_withValidDetails_thenReturnUpdatedList() throws Exception {
        TaskListDTO dto = new TaskListDTO();
        dto.setName("new name");
        given(mockListService.update(isA(TaskListDTO.class))).willReturn(Optional.of(dto));

        mvc.perform(patch("/lists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto))
        )
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(dto)));
        dto.setId(1L);
        verify(mockListService).update(argThat(new TaskListDTOId_NameMatcher(dto)));
    }

    @Test
    @WithUserDetails("user")
    void when_updateList_withInvalidId_thenReturn404() throws Exception {
        TaskListDTO dto = new TaskListDTO();
        dto.setId(1L);
        dto.setName("name");
        mvc.perform(patch("/lists/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto))
        )
                .andExpect(status().isNotFound());
        // Controller should properly set list ID (i.e. not use any submitted one)
        dto.setId(99999L);
        verify(mockListService).update(argThat(new TaskListDTOId_NameMatcher(dto)));
    }

    @Test
    @WithUserDetails("user")
    void when_updateList_withInvalidDetails_thenReturn400() throws Exception {
        TaskListDTO dto = new TaskListDTO();
        dto.setName("a".repeat(300));
        given(mockListService.update(isA(TaskListDTO.class))).willReturn(Optional.of(dto));

        mvc.perform(patch("/lists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto))
        )
                .andExpect(status().isBadRequest());
        verify(mockListService, times(0)).update(isA(TaskListDTO.class));
    }

    @Test
    @WithUserDetails("user")
    void when_updateList_withNoBody_thenReturn400() throws Exception {
        mvc.perform(patch("/lists/1"))
                .andExpect(status().isBadRequest());
        verify(mockListService, times(0)).update(isA(TaskListDTO.class));
    }

    @Test
    @WithUserDetails("user")
    void when_getViewers_thenReturnServiceResults_mappedToUsernames() throws Exception {
        List<String> resultList = List.of("user");
        Set<UserDTO> userSet = Set.of(new UserDTO("user@email.com", "user", "password"));
        TaskListDTO mockDTO = mock(TaskListDTO.class);

        given(mockListService.findById(anyLong())).willReturn(Optional.of(mockDTO));
        given(mockDTO.getViewers()).willReturn(userSet);

        mvc.perform(get("/lists/1/share"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(resultList)));
        verify(mockListService).findById(1L);
        verify(mockDTO).getViewers();
    }

    @Test
    @WithUserDetails("user")
    void when_shareList_withValidDetails_thenReturn200() throws Exception {
        given(mockListService.existsByIdAndViewerUsername(anyLong(), anyString())).willReturn(false);
        given(mockListService.addViewer(anyLong(), anyString())).willReturn(true);
        String username = "username";

        mvc.perform(post("/lists/1/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new ListShareDTO(username))))
                .andExpect(status().isOk());
        verify(mockListService).existsByIdAndViewerUsername(1L, username);
        verify(mockListService).addViewer(1L, username);
    }

    @Test
    @WithUserDetails("user")
    void when_shareList_withInvalidId_thenReturn404() throws Exception {
        given(mockListService.existsByIdAndViewerUsername(anyLong(), anyString())).willReturn(false);
        given(mockListService.addViewer(anyLong(), anyString())).willReturn(false);
        String username = "username";

        mvc.perform(post("/lists/1/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new ListShareDTO(username))))
                .andExpect(status().isNotFound());
        verify(mockListService).existsByIdAndViewerUsername(1L, username);
        verify(mockListService).addViewer(1L, username);
    }

    @Test
    @WithUserDetails("user")
    void when_shareList_withInvalidUsername_thenReturn400() throws Exception {
        given(mockListService.existsByIdAndViewerUsername(anyLong(), anyString())).willReturn(true);
        String username = "username";

        mvc.perform(post("/lists/1/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new ListShareDTO(username))))
                .andExpect(status().isBadRequest());
        verify(mockListService).existsByIdAndViewerUsername(1L, username);
        verify(mockListService, times(0)).addViewer(anyLong(), anyString());
    }

    @Test
    @WithUserDetails("user")
    void when_shareList_withNoBody_thenReturn400() throws Exception {
        mvc.perform(post("/lists/1/share"))
                .andExpect(status().isBadRequest());
        verify(mockListService, times(0)).existsByIdAndViewerUsername(anyLong(), anyString());
        verify(mockListService, times(0)).addViewer(anyLong(), anyString());
    }

    @Test
    @WithUserDetails("user")
    void when_unshareList_withValidId_thenReturn200() throws Exception {
        given(mockListService.removeViewer(anyLong(), anyLong())).willReturn(true);
        mvc.perform(delete("/lists/1/share"))
                .andExpect(status().isOk());
        verify(mockListService).removeViewer(1L, 1L);
    }

    @Test
    @WithUserDetails("user")
    void when_unshareList_withInvalidId_thenReturn404() throws Exception {
        given(mockListService.removeViewer(anyLong(), anyLong())).willReturn(false);
        mvc.perform(delete("/lists/1/share"))
                .andExpect(status().isNotFound());
        verify(mockListService).removeViewer(1L, 1L);
    }

    @Test
    @WithUserDetails("user")
    void when_unshareAll_withValidId_thenReturn200() throws Exception {
        given(mockListService.removeAllViewers(anyLong())).willReturn(true);
        mvc.perform(post("/lists/2/unshare"))
                .andExpect(status().isOk());
        verify(mockListService).removeAllViewers(2L);
    }

    @Test
    @WithUserDetails("user")
    void when_unshareAll_withInvalidId_thenReturn404() throws Exception {
        given(mockListService.removeAllViewers(anyLong())).willReturn(false);
        mvc.perform(post("/lists/2/unshare"))
                .andExpect(status().isNotFound());
        verify(mockListService).removeAllViewers(2L);
    }

    @Test
    @WithUserDetails("user")
    void when_completeList_withValidId_thenReturn200() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setState(Task.State.TASK_ARCHIVED);

        mvc.perform(post("/lists/3/complete"))
                .andExpect(status().isOk());
        verify(mockTaskService).updateAllByList(eq(3L), argThat(new TaskDTOStateMatcher(dto)));
    }

    @Test
    @WithUserDetails("user")
    void when_completeList_withInvalidId_thenReturn404() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setState(Task.State.TASK_ARCHIVED);
        doThrow(NotFoundException.class).when(mockTaskService).updateAllByList(anyLong(), isA(TaskDTO.class));
        mvc.perform(post("/lists/3/complete"))
                .andExpect(status().isNotFound());
        verify(mockTaskService).updateAllByList(eq(3L), argThat(new TaskDTOStateMatcher(dto)));
    }

    @Test
    @WithUserDetails("user")
    void when_clearList_withValidId_thenReturn200() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setState(Task.State.TASK_INBOX);

        mvc.perform(post("/lists/4/clear"))
                .andExpect(status().isOk());
        verify(mockTaskService).updateAllByList(eq(4L), argThat(new TaskDTOStateMatcher(dto)));
    }

    @Test
    @WithUserDetails("user")
    void when_clearList_withInvalidId_thenReturn404() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setState(Task.State.TASK_INBOX);
        doThrow(NotFoundException.class).when(mockTaskService).updateAllByList(anyLong(), isA(TaskDTO.class));
        mvc.perform(post("/lists/4/clear"))
                .andExpect(status().isNotFound());
        verify(mockTaskService).updateAllByList(eq(4L), argThat(new TaskDTOStateMatcher(dto)));
    }

    @Test
    @WithUserDetails("user")
    void when_deleteAllLists_thenReturn200() throws Exception {
        mvc.perform(delete("/lists"))
                .andExpect(status().isOk());
        verify(mockListService).deleteAllByOwnerId(1L);
    }

    @Test
    @WithUserDetails("user")
    void when_deleteList_withValidId_thenReturn200() throws Exception {
        when(mockListService.deleteById(anyLong())).thenReturn(true);
        mvc.perform(delete("/lists/5"))
                .andExpect(status().isOk());
        verify(mockListService).deleteById(5L);
    }

    @Test
    @WithUserDetails("user")
    void when_deleteList_withInvalidId_thenReturn404() throws Exception {
        when(mockListService.deleteById(anyLong())).thenReturn(false);
        mvc.perform(delete("/lists/5"))
                .andExpect(status().isNotFound());
        verify(mockListService).deleteById(5L);
    }

    @Test
    @WithUserDetails("user")
    void when_createTask_withValidDetails_thenReturnServiceResults() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setListId(6L);
        dto.setState(Task.State.TASK_INBOX);
        dto.setName(null);
        when(mockTaskService.create(isA(TaskDTO.class))).thenReturn(dto);

        MvcResult result = mvc.perform(post("/lists/6/tasks"))
                .andExpect(status().isCreated())
                .andReturn();
        TaskDTO resultDTO = new ObjectMapper()
                .disable(MapperFeature.USE_ANNOTATIONS)
                .readValue(result.getResponse().getContentAsString(), TaskDTO.class);

        verify(mockTaskService).create(argThat(new TaskDTOListId_NullName_StateMatcher(dto)));
        assertEquals(6L, resultDTO.getListId());
        assertEquals(Task.State.TASK_INBOX, resultDTO.getState());
        assertNull(resultDTO.getName());
    }
}

// Argument matchers: each checks that specific DTO fields match the one they're initialised with

@AllArgsConstructor
class TaskDTOStateMatcher implements ArgumentMatcher<TaskDTO> {
    private final TaskDTO left;
    @Override
    public boolean matches(TaskDTO right) {
        return left.getState().equals(right.getState());
    }
}

@AllArgsConstructor
class TaskListDTOId_NameMatcher implements ArgumentMatcher<TaskListDTO> {
    private final TaskListDTO left;
    @Override
    public boolean matches(TaskListDTO right) {
        return left.getId().equals(right.getId())
                && left.getName().matches(right.getName());
    }
}

@AllArgsConstructor
class TaskDTOListId_NullName_StateMatcher implements ArgumentMatcher<TaskDTO> {
    private final TaskDTO left;
    @Override
    public boolean matches(TaskDTO right) {
        return left.getState().equals(right.getState())
                && left.getListId().equals(right.getListId())
                && right.getName() == null;
    }
}