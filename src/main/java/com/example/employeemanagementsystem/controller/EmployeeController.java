package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.repository.*;
import com.example.employeemanagementsystem.dto.EmployeeRequest;
import com.example.employeemanagementsystem.dto.EmployeeSearchRequestDTO;
import com.example.employeemanagementsystem.entity.Department;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.exception.ManagerAlreadyExistsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.UserAlreadyExistsException;
import com.example.employeemanagementsystem.service.EmployeeService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling employee-related API requests.
 */
@RestController
@RequestMapping("api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Retrieves employees based on the role of the authenticated user.
     * - ADMIN: All employees
     * - MANAGER: Employees under the manager
     *
     * @param authentication contains user information
     * @return List of employees or error response
     */
    @GetMapping
    public ResponseEntity<?> getEmployees(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Received request to fetch employees for user: {}", username);

        try {
            String role = employeeService.getUserRole(username);
            List<Employee> employees;

            switch (role) {
                case "ROLE_ADMIN":
                    employees = employeeService.getAllEmployees();
                    break;
                case "ROLE_MANAGER":
                    employees = employeeService.getEmployeesForManager(username);
                    break;
                default:
                    String message = "Access denied: unauthorized role for user " + username;
                    logger.warn(message);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", message));
            }

            logger.info("Returning {} employees for user: {}", employees.size(), username);
            return ResponseEntity.ok(employees);

        } catch (Exception e) {
            logger.error("Failed to retrieve employees for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Internal server error while fetching employees"));
        }
    }

    /**
     * Retrieves an employee by ID with role-based access:
     * - ADMIN: Can access any employee
     * - MANAGER: Can access only authorized employees
     *
     * @param id ID of the employee to retrieve
     * @param authentication current authenticated user
     * @return Employee data or error
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        logger.info("Received request to fetch employee with ID: {} by user: {}", id, username);

        try {
            String role = employeeService.getUserRole(username);
            Optional<Employee> employee;

            if ("ROLE_ADMIN".equals(role)) {
                employee = employeeService.getEmployeeById(id);
            } else if ("ROLE_MANAGER".equals(role)) {
                employee = employeeService.getEmployeeByIdForManager(id, username);
            } else {
                logger.warn("Access denied for user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            logger.info("Returning employee with ID: {}", id);
            return ResponseEntity.ok(employee.get());

        } catch (EmployeeNotFoundException e) {
            logger.warn("Employee not found with ID: {}", id);
            throw e; // handled by global exception handler
        } catch (Exception e) {
            logger.error("Unexpected error while fetching employee with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds a new employee with associated role and department.
     * Validates the input request body.
     *
     * @param employeeRequest contains new employee data
     * @return Created employee object or error
     */
    @PostMapping("/addEmployee")
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        logger.info("Received request to add employee with email: {}", employeeRequest.getEmail());

        try {
            Employee employee = new Employee();
            employee.setFirstName(employeeRequest.getFirstName());
            employee.setLastName(employeeRequest.getLastName());
            employee.setEmail(employeeRequest.getEmail());
            employee.setPhoneNumber(employeeRequest.getPhoneNumber());
            employee.setJobTitle(employeeRequest.getJobTitle());
            employee.setSalary(employeeRequest.getSalary());
            employee.setHireDate(employeeRequest.getHireDate());
            employee.setDateOfBirth(employeeRequest.getDateOfBirth());
            employee.setGender(employeeRequest.getGender());

            String roleName = employeeRequest.getRole();
            Long departmentId = employeeRequest.getDepartment().getId();

            Employee savedEmployee = employeeService.addEmployee(employee, roleName, departmentId);
            logger.info("Employee created successfully with ID: {}", savedEmployee.getId());
            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);

        } catch (UserAlreadyExistsException | ResourceNotFoundException | ManagerAlreadyExistsException e) {
            logger.warn("Failed to add employee: {}", e.getMessage());
            throw e; // handled by global exception handler
        } catch (Exception e) {
            logger.error("Unexpected error while adding employee", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing employee's details by ID.
     *
     * @param id employee ID to update
     * @param employeeDetails updated employee data
     * @return Updated employee or error
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employeeDetails) {
        logger.info("Getting request to update employee with ID: {}", id);
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(updatedEmployee);
        } catch (ResourceNotFoundException ex) {
            logger.warn("Employee not found for update with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating employee with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Fetches the profile of the currently authenticated employee.
     *
     * @param authentication current user
     * @return Employee profile or error
     */
    @GetMapping("/profile")
    public ResponseEntity<Employee> getEmployeeProfile(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Getting request to Fetching profile for user: {}", username);
        try {
            Optional<Employee> employee = employeeRepository.findByEmail(username);
            if (employee.isEmpty()) {
                logger.warn("Profile not found for user: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            logger.info("Profile fetched successfully for user", username);
            return ResponseEntity.ok(employee.get());
        } catch (Exception e) {
            logger.error("Error fetching profile for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes an employee by ID.
     *
     * @param id ID of the employee to delete
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        logger.info("Getting request to delete employee with ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches employees based on dynamic filter criteria in the request body.
     * Allows search only for ADMIN and MANAGER roles.
     *
     * @param searchRequest contains filtering parameters
     * @param authentication current user
     * @return Filtered list of employees
     */
    @PostMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(@Valid @RequestBody EmployeeSearchRequestDTO searchRequest, Authentication authentication) {
        String username = authentication.getName();
        logger.info("Getting request to search employees for user: {}", username);
        try {
            List<Employee> employees = employeeService.searchEmployees(searchRequest, username);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            logger.error("Error searching employees for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
