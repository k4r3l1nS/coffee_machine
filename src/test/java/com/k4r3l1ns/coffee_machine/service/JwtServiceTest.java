package com.k4r3l1ns.coffee_machine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;

    // base64 secret >= 256 bit
    private static final String SECRET =
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWY=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);
        ReflectionTestUtils.setField(
                jwtService,
                "jwtSigningKey",
                SECRET
        );
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("john");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUserName_shouldReturnUsername() {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("john");

        String token = jwtService.generateToken(user);

        String username = jwtService.extractUserName(token);

        assertEquals("john", username);
    }

    @Test
    void isTokenValid_shouldReturnTrue() {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("john");

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_shouldReturnFalseForAnotherUser() {
        UserDetails user1 = mock(UserDetails.class);
        when(user1.getUsername()).thenReturn("john");

        UserDetails user2 = mock(UserDetails.class);
        when(user2.getUsername()).thenReturn("mike");

        String token = jwtService.generateToken(user1);

        assertFalse(jwtService.isTokenValid(token, user2));
    }

    @Test
    void extractUserName_shouldThrowForInvalidToken() {
        assertThrows(
                Exception.class,
                () -> jwtService.extractUserName("invalid.token")
        );
    }
}