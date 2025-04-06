package com.example.employeemanagementsystem.exception;

/**
 * Custom exception thrown when a user tries to access a resource or perform an operation 
 * they are not authorized for.
 *
 * Typically used in role-based access control checks.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs a new AccessDeniedException with the specified detail message.
     *
     * @param message the detail message explaining the access restriction
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}
