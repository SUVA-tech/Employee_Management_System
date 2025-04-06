package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related endpoints such as signup and login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Logger for logging information and error messages
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * Endpoint for user registration.
     * Accepts a User object in the request body and attempts to save it using the UserService.
     *
     * @param user the user to be registered
     * @return ResponseEntity indicating success or failure of registration
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        try {
            logger.info("Signup attempt for username: {}", user.getUsername());
            userService.saveUser(user);
            logger.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while registering user: {}", user.getUsername(), e);
            return ResponseEntity.internalServerError().body("User registration failed.");
        }
    }

    /**
     * Endpoint for login.
     * This method simply confirms the login process and can be used as a health check for authentication.
     *
     * @return ResponseEntity confirming successful login access
     */
    @GetMapping("/login")
    public ResponseEntity<String> login() {
        logger.info("Login endpoint accessed");
        return ResponseEntity.ok("Login successful");
    }
}
