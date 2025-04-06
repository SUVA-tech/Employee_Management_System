package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.exception.UserAlreadyExistsException;
import com.example.employeemanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername("test@example.com");
    }

    // ---------------------------------------
    // findByUsername()
    // ---------------------------------------

    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));
        Optional<User> result = userService.findByUsername("test@example.com");
        assertThat(result).isPresent().contains(testUser);
    }

    @Test
    void testFindByUsername_Exception() {
        when(userRepository.findByUsername("test@example.com"))
            .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> userService.findByUsername("test@example.com"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to fetch user details");
    }

    // ---------------------------------------
    // saveUser()
    // ---------------------------------------

    @Test
    void testSaveUser_Success_NewUser() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(testUser)).thenReturn(testUser);

        User saved = userService.saveUser(testUser);
        assertThat(saved).isEqualTo(testUser);
    }

    @Test
    void testSaveUser_UserAlreadyExists() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.saveUser(testUser))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("Email is already registered");
    }

    @Test
    void testSaveUser_SaveException() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(testUser)).thenThrow(new RuntimeException("DB fail"));

        assertThatThrownBy(() -> userService.saveUser(testUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to save user");
    }
}
