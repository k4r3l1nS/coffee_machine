package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dao.UserRepository;
import com.k4r3l1ns.coffee_machine.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@test.com");
        user.setPassword("password");
    }

    @Test
    public void testSaveUser() {

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateUserSuccess() {

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(user);

        assertNotNull(createdUser);
        assertEquals("testUser", createdUser.getUsername());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateUserDuplicateUsername() {

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.create(user));

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testCreateUserDuplicateEmail() {

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.create(user));

        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void testGetByUsernameSuccess() {

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.getByUsername("testUser");

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    public void testGetByUsernameNotFound() {

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("unknownUser"));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknownUser");
    }

    @Test
    public void testUserDetailsService() {

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertNotNull(userService.userDetailsService().loadUserByUsername("testUser"));
        verify(userRepository, times(1)).findByUsername("testUser");
    }
}
