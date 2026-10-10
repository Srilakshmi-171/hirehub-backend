package com.hirehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hirehub.dto.RegisterUserRequest;
import com.hirehub.dto.UserResponse;
import com.hirehub.entity.User;
import com.hirehub.exception.InvalidUserIdException;
import com.hirehub.repository.UserRepository;
import com.hirehub.service.UserService;

@ExtendWith(MockitoExtension.class)


public class UserServiceTest {

	
	@Mock
	private UserRepository userRepository;
	@Mock 
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserService userService;
	

	@Test
	void invalidInputTest() {
		assertThrows(InvalidUserIdException.class, () -> userService.getUserById(-1L));

        // 2. Test ID 0 with assertThrows
        assertThrows(InvalidUserIdException.class, () -> userService.getUserById(0L));

        // 3. Verify userRepository.findById(...) was never called
        verify(userRepository, never()).findById(anyLong());
        
	}
	
	@Test
	void registerUserPasswordHashTest() {
	    // Arrange: Create request data
	    RegisterUserRequest request = new RegisterUserRequest();
	    request.setName("Test Candidate");
	    request.setPassword("TestPass123");
	    request.setEmail("test@example.com");
	    
	    // Arrange: Create simulated database persistence object
	    User savedUser = new User();
	    savedUser.setName(request.getName());
	    savedUser.setEmail(request.getEmail());
	    savedUser.setPassword("mocked-bcrypt-hash"); // 1. Password set to "mocked-bcrypt-hash"
	    
	    // Arrange: Define mock behavior (2. Kept strictly before the service call)
	    when(passwordEncoder.encode("TestPass123")).thenReturn("mocked-bcrypt-hash");
	    Mockito.when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
	    when(userRepository.save(any(User.class))).thenReturn(savedUser);
	    
	    // Act: Execute registration
	    UserResponse response = userService.registerUser(request);
	    
	 // 1. Create an ArgumentCaptor for the User entity
	    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

	    // 2. Verify repository save was called and capture the actual entity passed to it
	    verify(userRepository).save(userCaptor.capture());

	    // 3. Retrieve the captured entity that the service attempted to persist
	    User capturedUser = userCaptor.getValue();

	    // 4. Assert that the captured password is encrypted/hashed
	    assertEquals("mocked-bcrypt-hash", capturedUser.getPassword(), 
	        "The password sent to the database must be hashed!");

	    // 5. Assert that the captured password is NOT the plaintext raw password
	    assertNotEquals("TestPass123", capturedUser.getPassword(), 
	        "Security risk: Raw plaintext password should not be passed to the database!");
	    
	    verify(passwordEncoder).encode("TestPass123");

	}


		
}
