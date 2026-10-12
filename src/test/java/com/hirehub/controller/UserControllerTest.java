
package com.hirehub.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.hirehub.dto.UserResponse;
import com.hirehub.service.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private Authentication authentication;

    @Test
    void candidateCannotAccessAnotherUsersProfile() {
        // Arrange: logged-in candidate is user 10
        when(authentication.getPrincipal()).thenReturn(10L);
        when(authentication.getAuthorities()).thenAnswer(
                invocation -> List.of(
                        new SimpleGrantedAuthority("ROLE_CANDIDATE")
                )
        );

        // Act + Assert: candidate requests user 20's profile
        assertThrows(AccessDeniedException.class, () ->
                userController.getUser(20L, authentication)
        );
    }
    
    @Test
    void candidateCanAccessOwnProfile() {
        // 1. Configure authentication.getPrincipal() to return 10L
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(10L);

        // 2. Configure the authority as ROLE_CANDIDATE
        when(authentication.getAuthorities()).thenAnswer(
                invocation -> List.of(
                        new SimpleGrantedAuthority("ROLE_CANDIDATE")
                )
        );
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(10L);
        when(userService.getUserById(10L)).thenReturn(mockResponse);

        // Mock the service response so the controller completes successfully
        UserResponse response = userController.getUser(10L, authentication);

        assertEquals(10L, response.getId());
        verify(userService).getUserById(10L);
    }
    
    @Test
    void adminCanAccessAnotherUsersProfile() {
    	Authentication authentication = mock(Authentication.class);
    	when(authentication.getPrincipal()).thenReturn(201L);

        // 2. Configure the authority as ROLE_CANDIDATE
        when(authentication.getAuthorities()).thenAnswer(
                invocation -> List.of(
                		new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );
        
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(10L);
        when(userService.getUserById(10L)).thenReturn(mockResponse);

        // Mock the service response so the controller completes successfully
        UserResponse response = userController.getUser(10L, authentication);

        assertEquals(10L, response.getId());
        verify(userService).getUserById(10L);
    }
}
