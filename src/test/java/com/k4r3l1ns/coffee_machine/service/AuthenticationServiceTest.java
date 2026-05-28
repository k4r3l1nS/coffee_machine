package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dto.JwtAuthenticationResponse;
import com.k4r3l1ns.coffee_machine.dto.SignInRequest;
import com.k4r3l1ns.coffee_machine.dto.SignUpRequest;
import com.k4r3l1ns.coffee_machine.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void signUp_shouldCreateUserAndReturnToken() {

        SignUpRequest request = new SignUpRequest();
        request.setUsername("john");
        request.setEmail("john@test.com");
        request.setPassword("12345");

        when(passwordEncoder.encode("12345"))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        JwtAuthenticationResponse response =
                authenticationService.signUp(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userService).create(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("john", savedUser.getUsername());
        assertEquals("john@test.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(passwordEncoder).encode("12345");
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void signIn_shouldAuthenticateAndReturnToken() {

        SignInRequest request = new SignInRequest();
        request.setUsername("john");
        request.setPassword("12345");

        User user = User.builder()
                .username("john")
                .password("encoded")
                .build();

        UserDetailsService uds = mock(UserDetailsService.class);

        when(userService.userDetailsService())
                .thenReturn(uds);

        when(uds.loadUserByUsername("john"))
                .thenReturn(user);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        JwtAuthenticationResponse response =
                authenticationService.signIn(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));

        verify(uds)
                .loadUserByUsername("john");

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void signIn_shouldThrowWhenAuthenticationFails() {

        SignInRequest request = new SignInRequest();
        request.setUsername("john");
        request.setPassword("wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(
                        UsernamePasswordAuthenticationToken.class));

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.signIn(request)
        );

        verify(userService, never())
                .userDetailsService();

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void signUp_shouldThrowWhenRequestInvalid() {

        SignUpRequest request = mock(SignUpRequest.class);

        doThrow(new IllegalArgumentException("invalid"))
                .when(request)
                .throwIfInvalid();

        assertThrows(
                IllegalArgumentException.class,
                () -> authenticationService.signUp(request)
        );

        verifyNoInteractions(
                userService,
                jwtService,
                passwordEncoder
        );
    }

    @Test
    void signIn_shouldThrowWhenRequestInvalid() {

        SignInRequest request = mock(SignInRequest.class);

        doThrow(new IllegalArgumentException("invalid"))
                .when(request)
                .throwIfInvalid();

        assertThrows(
                IllegalArgumentException.class,
                () -> authenticationService.signIn(request)
        );

        verifyNoInteractions(
                authenticationManager,
                jwtService,
                userService
        );
    }
}