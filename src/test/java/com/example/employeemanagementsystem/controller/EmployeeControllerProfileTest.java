package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerProfileTest {

    // Injects mock dependencies into EmployeeController
    @InjectMocks
    private EmployeeController employeeController;

    // Mocked EmployeeRepository dependency
    @Mock
    private EmployeeRepository employeeRepository;

    // Mocked Authentication object to simulate user authentication
    @Mock
    private Authentication authentication;

    private Employee sampleEmployee;

    // Setup executed before each test case
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Creating a sample employee object to be returned in tests
        sampleEmployee = new Employee();
        sampleEmployee.setId(1L);
        sampleEmployee.setFirstName("John");
        sampleEmployee.setLastName("Doe");
        sampleEmployee.setEmail("john.doe@example.com");
    }

    /**
     * Test case: Successfully fetches employee profile using authenticated user's email.
     * Mocks authentication and repository to return the employee.
     */
    @Test
    void testGetEmployeeProfile_Success() {
        String email = "john.doe@example.com";

        when(authentication.getName()).thenReturn(email);
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(sampleEmployee));

        ResponseEntity<Employee> response = employeeController.getEmployeeProfile(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(email, response.getBody().getEmail());
    }

    /**
     * Test case: Returns 403 Forbidden when employee is not found by email.
     * Simulates a missing employee scenario.
     */
    @Test
    void testGetEmployeeProfile_NotFound() {
        String email = "missing@example.com";

        when(authentication.getName()).thenReturn(email);
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<Employee> response = employeeController.getEmployeeProfile(authentication);

        assertEquals(403, response.getStatusCodeValue());
    }

    /**
     * Test case: Returns 500 Internal Server Error on unexpected exception (e.g., DB failure).
     * Simulates an exception being thrown during data access.
     */
    @Test
    void testGetEmployeeProfile_InternalServerError() {
        String email = "error@example.com";

        when(authentication.getName()).thenReturn(email);
        when(employeeRepository.findByEmail(email)).thenThrow(new RuntimeException("DB down"));

        ResponseEntity<Employee> response = employeeController.getEmployeeProfile(authentication);

        assertEquals(500, response.getStatusCodeValue());
    }
}
