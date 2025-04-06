package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.exception.UserAlreadyExistsException;
import com.example.employeemanagementsystem.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    // Logger for monitoring and debugging
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetch a user by their username (email).
     *
     * @param username the username (email) to search for
     * @return Optional<User> if found, or empty if not
     * @throws RuntimeException if an error occurs during the lookup
     */
    public Optional<User> findByUsername(String username) {
        try {
            logger.info("Searching for user by username: {}", username);
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error occurred while searching for user: {}", username, e);
            throw new RuntimeException("Failed to fetch user details");
        }
    }

    /**
     * Save a new user after checking if the email is already registered.
     *
     * @param user the user to be saved
     * @return the saved user object
     * @throws UserAlreadyExistsException if the email is already registered
     * @throws RuntimeException if saving fails due to unexpected error
     */
    public User saveUser(User user) {
        try {
            logger.info("Checking if user already exists by email (used as username): {}", user.getUsername());

            // Check if user already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                logger.warn("User with email {} already exists", user.getUsername());
                throw new UserAlreadyExistsException("Email is already registered");
            }

            // Save new user
            logger.info("Saving new user: {}", user.getUsername());
            return userRepository.save(user);
        } catch (UserAlreadyExistsException ex) {
            // Let global exception handler manage known exceptions
            throw ex;
        } catch (Exception e) {
            logger.error("Error occurred while saving user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to save user");
        }
    }
}
