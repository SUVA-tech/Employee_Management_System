package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.exception.UserAlreadyExistsException;
import com.example.employeemanagementsystem.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    // Injects mocked dependencies into the AuthController instance
    @InjectMocks
    private AuthController authController;

    // Mocked instance of the UserService
    @Mock
    private UserService userService;

    private User testUser;

    // Initializes test data and mocks before each test method
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Creating a sample user object
        testUser = new User();
        testUser.setUsername("johndoe@example.com");
        testUser.setPassword("securePassword");
    }

    /**
     * Test case: Successful user signup.
     * Mocks the saveUser() method to return the given user.
     * Verifies that a success response is returned.
     */
    @Test
    void testSignup_Success() {
        when(userService.saveUser(testUser)).thenReturn(testUser);

        ResponseEntity<String> response = authController.signup(testUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully!", response.getBody());
        verify(userService, times(1)).saveUser(testUser);
    }

    /**
     * Test case: Signup fails when user already exists.
     * Mocks saveUser() to throw UserAlreadyExistsException.
     * Verifies that a proper error message and status code are returned.
     */
    @Test
    void testSignup_UserAlreadyExists() {
        when(userService.saveUser(testUser)).thenThrow(new UserAlreadyExistsException("Email is already registered"));

        ResponseEntity<String> response = authController.signup(testUser);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("User registration failed.", response.getBody());
        verify(userService, times(1)).saveUser(testUser);
    }

    /**
     * Test case: Signup fails due to an unexpected internal server error.
     * Mocks saveUser() to throw a generic RuntimeException.
     * Verifies that a generic failure response is returned.
     */
    @Test
    void testSignup_InternalServerError() {
        when(userService.saveUser(testUser)).thenThrow(new RuntimeException("Database failure"));

        ResponseEntity<String> response = authController.signup(testUser);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("User registration failed.", response.getBody());
        verify(userService, times(1)).saveUser(testUser);
    }

    /**
     * Test case: Login endpoint returns a static success response.
     * Verifies the response status and body.
     */
    @Test
    void testLogin() {
        ResponseEntity<String> response = authController.login();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody());
    }
}
