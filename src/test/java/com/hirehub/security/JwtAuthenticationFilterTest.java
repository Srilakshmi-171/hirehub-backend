
package com.hirehub.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hirehub.entity.AccountStatus;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(
                jwtService,
                userRepository,
                objectMapper
        );
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void activeUserShouldBeAuthenticated() throws Exception {

        User user = new User();
        user.setId(101L);
        user.setRole("CANDIDATE");
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(jwtService.extractUserId("valid-token"))
                .thenReturn(101L);

        when(userRepository.findById(101L))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNotNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        assertEquals(
                101L,
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal()
        );
    }

    @Test
    void inactiveUserShouldBeRejected() throws Exception {

        User user = new User();
        user.setId(101L);
        user.setRole("CANDIDATE");
        user.setAccountStatus(AccountStatus.INACTIVE);

        when(jwtService.extractUserId("valid-token"))
                .thenReturn(101L);

        when(userRepository.findById(101L))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void nonexistentUserShouldBeRejected() throws Exception {

        when(jwtService.extractUserId("valid-token"))
                .thenReturn(101L);

        when(userRepository.findById(101L))
                .thenReturn(Optional.empty());

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }
}
