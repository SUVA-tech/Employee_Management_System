package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.EmployeeSearchRequestDTO;
import com.example.employeemanagementsystem.entity.Department;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.entity.Role;
import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.exception.ManagerAlreadyExistsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.RoleNotFoundException;
import com.example.employeemanagementsystem.exception.UserAlreadyExistsException;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.RoleRepository;
import com.example.employeemanagementsystem.repository.UserRepository;
import com.example.employeemanagementsystem.specification.EmployeeSpecification;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Adds a new employee along with user credentials, role, and department assignment.
     * Also assigns the manager to a department if the role is 'ROLE_MANAGER'.
     */
    @Transactional
    public Employee addEmployee(Employee employee, String roleName, Long departmentId) {
        logger.info("Adding new employee: {}", employee.getEmail());

        // Check if a user already exists with the provided email
        Optional<User> existingUser = userRepository.findByUsername(employee.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("User already exists with email: {}", employee.getEmail());
            throw new UserAlreadyExistsException("User already exists with email: " + employee.getEmail());
        }

        // Create new user credentials
        User user = new User();
        user.setUsername(employee.getEmail());
        user.setPassword(passwordEncoder.encode("defaultPassword"));

        // Assign role to user
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> {
                    logger.error("Role '{}' not found", roleName);
                    return new RoleNotFoundException("Role '" + roleName + "' not found");
                });

        user.setRoles(Set.of(role));
        userRepository.save(user);
        employee.setUser(user);

        // Assign department to employee
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> {
                    logger.error("Department not found with ID: {}", departmentId);
                    return new EmployeeNotFoundException("Department not found with ID: " + departmentId);
                });
        employee.setDepartment(department);

        // If manager role, ensure no other manager is assigned to the department
        if ("ROLE_MANAGER".equals(roleName)) {
            if (department.hasManager()) {
                throw new ManagerAlreadyExistsException("Manager already exists for the department with ID: " + department.getId());
            }
            department.setManager(user);
            departmentRepository.save(department);
        }

        // Save and return the employee
        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Saved employee with ID: {}", savedEmployee.getId());

        return savedEmployee;
    }

    /**
     * Retrieves all employees along with their departments.
     */
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        List<Employee> employees = employeeRepository.findAllEmployeesWithDepartment();
        logger.info("Retrieved {} employees", employees.size());
        return employees;
    }

    /**
     * Retrieves employees who report to a specific manager.
     */
    public List<Employee> getEmployeesForManager(String managerUsername) {
        logger.info("Fetching employees for manager: {}", managerUsername);
        return employeeRepository.findEmployeesByManager(managerUsername);
    }

    /**
     * Fetches a specific employee by ID (with department info), or throws exception if not found.
     */
    public Optional<Employee> getEmployeeById(Long id) {
        logger.info("Fetching employee by ID: {}", id);
        return Optional.ofNullable(employeeRepository.findEmployeeWithDepartment(id)
                .orElseThrow(() -> {
                    String errorMessage = "Employee not found with ID: " + id;
                    logger.error(errorMessage);
                    return new EmployeeNotFoundException(errorMessage);
                }));
    }

    /**
     * Fetches employee by ID for a specific manager (validates manager's ownership).
     */
    public Optional<Employee> getEmployeeByIdForManager(Long id, String username) {
        logger.info("Fetching employee by ID: {} for manager {}:", id, username);
        return Optional.ofNullable(employeeRepository.findEmployeeByIdForManager(id, username)
                .orElseThrow(() -> {
                    String errorMessage = "Employee not found with ID: " + id;
                    logger.error(errorMessage);
                    return new EmployeeNotFoundException(errorMessage);
                }));
    }

    /**
     * Updates the details of an existing employee.
     */
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        // Retrieve employee
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Update employee fields
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setJobTitle(employeeDetails.getJobTitle());
        employee.setSalary(employeeDetails.getSalary());
        employee.setHireDate(employeeDetails.getHireDate());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());

        // Update department if changed
        if (employeeDetails.getDepartment() != null && employeeDetails.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(employeeDetails.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + employeeDetails.getDepartment().getId()));
            employee.setDepartment(department);
        }

        // Save and return updated employee
        return employeeRepository.save(employee);
    }

    /**
     * Deletes an employee and their associated user account.
     * If the employee is a manager, the department is updated accordingly.
     */
    @Transactional
    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with ID: {}", id);

        // Find employee
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> {
            logger.error("Employee not found with ID: {}", id);
            return new EmployeeNotFoundException("Employee not found with ID: " + id);
        });

        User user = employee.getUser();

        // If the employee is a manager, remove manager from the department
        Department department = departmentRepository.findByManager(user);
        if (department != null) {
            department.setManager(null);
            departmentRepository.save(department);
            logger.info("Removed manager from department: {}", department.getName());
        }

        // Delete employee and their user account
        employeeRepository.deleteById(id);
        userRepository.deleteById(user.getId());

        logger.info("Deleted employee and associated user account");
    }

    /**
     * Searches employees by department name.
     */
    public List<Employee> searchByDepartment(String departmentName) {
        logger.info("Searching employees by department: {}", departmentName);
        return employeeRepository.findByDepartment_Name(departmentName);
    }

    /**
     * Searches employees by job title.
     */
    public List<Employee> searchByJobTitle(String jobTitle) {
        logger.info("Searching employees by job title: {}", jobTitle);
        return employeeRepository.findByJobTitle(jobTitle);
    }

    /**
     * Searches employees using filters (job title, salary, department, etc.) with role-based access.
     */
    public List<Employee> searchEmployees(EmployeeSearchRequestDTO searchRequest, String username) {
        logger.info("Searching employees by: {} for user: {}", searchRequest, username);

        String role = getUserRole(username);

        // Check if specified department exists
        if (searchRequest.getDepartmentId() != null) {
            boolean deptExists = departmentRepository.existsById(searchRequest.getDepartmentId());
            if (!deptExists) {
                logger.warn("No department found with ID: {}", searchRequest.getDepartmentId());
                return List.of(); // Return empty list if department doesn't exist
            }
        }

        // Apply filtering logic based on role
        if ("ROLE_ADMIN".equals(role)) {
            logger.info("Admin role detected - returning all matching employees");
            return employeeRepository.findAll(EmployeeSpecification.filterByCriteria(searchRequest));
        } else if ("ROLE_MANAGER".equals(role)) {
            Department managerDepartment = departmentRepository.findByManagerUsername(username);
            logger.info("Manager role detected - returning employees from department: {}", managerDepartment.getName());
            return employeeRepository.findAll(EmployeeSpecification.filterByCriteriaAndDepartment(searchRequest, managerDepartment));
        } else {
            logger.error("Access denied for user: {}", username);
            throw new AccessDeniedException("Access Denied");
        }
    }

    /**
     * Retrieves the role of a user by their username.
     */
    public String getUserRole(String username) {
        String role = userRepository.findRoleByUsername(username);
        logger.info("Fetched role for user {}: {}", username, role);
        return role;
    }
}
