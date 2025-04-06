package com.example.employeemanagementsystem.exception;

/**
 * Exception thrown when an employee is not found in the system.
 *
 * Typically used in service layer methods that fetch employee data by ID.
 */
public class EmployeeNotFoundException extends RuntimeException {

    /**
     * Constructs a new EmployeeNotFoundException with a specific message.
     *
     * @param message Detailed message indicating the reason for the exception.
     */
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
