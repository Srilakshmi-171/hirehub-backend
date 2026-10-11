package com.hirehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hirehub.dto.LoginRequest;
import com.hirehub.dto.RegisterUserRequest;
import com.hirehub.dto.UserResponse;
import com.hirehub.entity.User;
import com.hirehub.exception.InvalidUserIdException;
import com.hirehub.repository.UserRepository;
import com.hirehub.security.JwtService;
import com.hirehub.service.AuthService;
import com.hirehub.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    // --- Mocks for UserService ---
    @Mock
    private UserRepository userRepository;

    @Mock 
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    // --- Service Under Test Injectors ---
    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    // ==========================================
    //            USER SERVICE TESTS
    // ==========================================

    @Test
    void invalidInputTest() {
        // Test ID -1 with assertThrows
        assertThrows(InvalidUserIdException.class, () -> userService.getUserById(-1L));

        // Test ID 0 with assertThrows
        assertThrows(InvalidUserIdException.class, () -> userService.getUserById(0L));

        // Verify userRepository.findById(...) was never called
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
        savedUser.setPassword("mocked-bcrypt-hash");
        
        // Arrange: Define mock behavior
        when(passwordEncoder.encode("TestPass123")).thenReturn("mocked-bcrypt-hash");
        Mockito.when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // Act: Execute registration
        UserResponse response = userService.registerUser(request);
        
        // Create an ArgumentCaptor for the User entity
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Verify repository save was called and capture the actual entity passed to it
        verify(userRepository).save(userCaptor.capture());

        // Retrieve the captured entity that the service attempted to persist
        User capturedUser = userCaptor.getValue();

        // Assert that the captured password is encrypted/hashed
        assertEquals("mocked-bcrypt-hash", capturedUser.getPassword(), 
            "The password sent to the database must be hashed!");

        // Assert that the captured password is NOT the plaintext raw password
        assertNotEquals("TestPass123", capturedUser.getPassword(), 
            "Security risk: Raw plaintext password should not be passed to the database!");
        
        verify(passwordEncoder).encode("TestPass123");
    }

    // ==========================================
    //            AUTH SERVICE TESTS
    // ==========================================

    @Test
    void loginWithWrongPasswordShouldThrowException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("candidate@example.com");
        request.setPassword("WrongPass123");

        User user = new User();
        user.setId(1L);
        user.setEmail("candidate@example.com");
        user.setPassword("stored-bcrypt-hash");

        when(userRepository.findByEmail("candidate@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("WrongPass123", "stored-bcrypt-hash"))
                .thenReturn(false);

        // Act and Assert
        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        // Verify no token is generated for a wrong password
        verify(jwtService, never()).generateToken(user);
    }
    
    @Test
    void loginWithUnknownEmailShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("SomePass123");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    @Test
    void loginWithCorrectPasswordShouldReturnToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("candidate@example.com");
        request.setPassword("CorrectPass123");

        User user = new User();
        user.setId(1L);
        user.setEmail("candidate@example.com");
        user.setPassword("stored-bcrypt-hash");

        when(userRepository.findByEmail("candidate@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("CorrectPass123", "stored-bcrypt-hash"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("mock-jwt-token");

        var response = authService.login(request);

        assertEquals("mock-jwt-token", response.getToken());
        verify(jwtService).generateToken(user);
    }
}
