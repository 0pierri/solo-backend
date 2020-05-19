package com.fdmgroup.backend.controller;

import com.fdmgroup.backend.model.UserDTO;
import com.fdmgroup.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class SignupControllerTest {

	@Mock
	UserService mockService;
	@Mock
	BindingResult bindingResult;
	@Mock
	BindingResult withErrorsBindingResult;
	@Mock
	BCryptPasswordEncoder encoder;
	SignupController controller;
	UserDTO dto;

	@BeforeEach
	public void initMock() {
		MockitoAnnotations.initMocks(this);
		controller = new SignupController(mockService, encoder);
		dto = new UserDTO("email", "username", "password");

		when(mockService.create(dto))
				.thenReturn((dto));
		when(bindingResult.hasErrors())
				.thenReturn(false);
		when(withErrorsBindingResult.hasErrors())
				.thenReturn(true);
	}

//	@Test
//	public void when_doRegister_callService_createUser() {
//		controller.handleSignup(dto, bindingResult);
//		verify(mockService).create(dto);
//	}
//
//	@Test
//	public void when_doRegister_withValidDetails_thenCreateAndReturnUser() {
//		UserDTO result = controller.handleSignup(dto, bindingResult);
//		assertEquals(dto, result);
//	}
//
//	@Test
//	public void when_doRegister_withFailedValidation_thenDontCallService_andThrowInvalidRequestException() {
//		dto.setUsername("");
//		assertThrows(InvalidRequestException.class, () -> controller.handleSignup(dto, withErrorsBindingResult));
//		verify(mockService, never()).create(dto);
//	}
//
//	@Test
//	public void when_doRegister_withDuplicateUsername_thenCallService() {
//		controller.handleSignup(dto, bindingResult);
//		controller.handleSignup(dto, bindingResult);
//
//		verify(mockService, times(2)).create(dto);
//	}
//
//	@Test
//	public void when_doRegister_receivesEmptyOptionalFromService_thenThrowInvalidRequestException() {
//		when(mockService.create(dto)).thenThrow(new InvalidRequestException(""));
//		assertThrows(InvalidRequestException.class, () -> controller.handleSignup(dto, bindingResult));
//	}

}
