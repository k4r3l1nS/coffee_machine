package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.config.NoSecurityConfig;
import com.k4r3l1ns.coffee_machine.dto.JwtAuthenticationResponse;
import com.k4r3l1ns.coffee_machine.dto.SignInRequest;
import com.k4r3l1ns.coffee_machine.dto.SignUpRequest;
import com.k4r3l1ns.coffee_machine.service.AuthenticationService;
import com.k4r3l1ns.coffee_machine.service.JwtService;
import com.k4r3l1ns.coffee_machine.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@Import(NoSecurityConfig.class)
public class AuthControllerTests {

    @Value("${auth.url}")
    private String authUrl;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    private JwtAuthenticationResponse jwtResponse;

    @BeforeEach
    public void setUp() {
        jwtResponse = new JwtAuthenticationResponse("test-jwt-token");
    }

    @Test
    public void testSignUpSuccess() throws Exception {

        when(authenticationService.signUp(any(SignUpRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/" + authUrl +"/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"test\", \"email\": \"test@test.com\",  \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    public void testSignInSuccess() throws Exception {

        when(authenticationService.signIn(any(SignInRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/" + authUrl +"/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"test\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }
}

