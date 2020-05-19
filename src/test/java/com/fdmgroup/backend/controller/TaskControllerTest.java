package com.fdmgroup.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.backend.model.TaskDTO;
import com.fdmgroup.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class TaskControllerTest {

    @MockBean
    TaskService mockTaskService;

    @InjectMocks
    TaskController controller;

    @Autowired
    WebApplicationContext context;
    MockMvc mvc;

    TaskDTO updateDto;
    TaskDTO resultDto;

    @BeforeEach
    void initMock() {
        MockitoAnnotations.initMocks(this);
        updateDto = new TaskDTO();
        resultDto = new TaskDTO();
        controller = new TaskController(mockTaskService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @WithUserDetails("user")
    void when_getTasks_thenReturnServiceResults() throws Exception {
        List<TaskDTO> dtoList = List.of(resultDto);
        given(mockTaskService.findAllByOwnerId(anyLong())).willReturn(dtoList);

        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(dtoList)));
        verify(mockTaskService).findAllByOwnerId(anyLong());
    }

    @Test
    void when_updateTask_receivesValidData_validationPasses() throws Exception {
        given(mockTaskService.update(isA(TaskDTO.class))).willReturn(Optional.of(resultDto));
        mvc.perform(patch("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateDto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void when_updateTask_receivesInvalidState_validationFails() throws Exception {
        given(mockTaskService.update(isA(TaskDTO.class)))
                .willReturn(Optional.of(resultDto));

        mvc.perform(patch("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"state\": \"INVALID_STATE\"}")
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().bytes(new byte[]{}));
    }

    @Test
    public void when_updateTask_receivesId_thenItIsIgnored() throws Exception {
        updateDto.setId(2L);
        resultDto.setId(1L);
        given(mockTaskService.update(isA(TaskDTO.class)))
                .willReturn(Optional.of(resultDto));

        mvc.perform(patch("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateDto))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void when_updateTask_receivesUnknownProperty_thenItIsIgnored() throws Exception {
        given(mockTaskService.update(isA(TaskDTO.class)))
                .willReturn(Optional.of(resultDto));

        mvc.perform(patch("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"someProperty\": \"\"}")
        )
                .andExpect(status().isOk());
    }
    //endregion

    //region Delete
    @Test
    void when_deleteTask_thenCallService_deleteTask() {
        given(mockTaskService.deleteById(anyLong()))
                .willReturn(true);

        controller.deleteTask(1);
        verify(mockTaskService).deleteById(1L);
    }

    @Test
    void when_deleteTask_withValidID_thenReturnOK() throws Exception {
        given(mockTaskService.deleteById(anyLong()))
                .willReturn(true);

        mvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void when_deleteTask_withInvalidId_thenReturnNotFound() throws Exception {
        given(mockTaskService.deleteById(anyLong()))
                .willReturn(false);

        mvc.perform(delete("/tasks/1"))
                .andExpect(status().isNotFound());
    }
    //endregion
}
