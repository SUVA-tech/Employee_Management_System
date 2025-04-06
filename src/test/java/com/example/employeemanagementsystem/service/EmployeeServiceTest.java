package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.EmployeeSearchRequestDTO;
import com.example.employeemanagementsystem.entity.*;
import com.example.employeemanagementsystem.exception.*;
import com.example.employeemanagementsystem.repository.*;
import com.example.employeemanagementsystem.specification.EmployeeSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    // Mock dependencies
    @Mock private EmployeeRepository employeeRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private Employee employee;
    private User user;
    private Role role;
    private Department department;

    // Setup mock entities before each test
    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_EMPLOYEE");

        department = new Department();
        department.setId(1L);
        department.setName("IT");

        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("test@example.com");
        employee.setUser(user);
        employee.setDepartment(department);
    }

    // Test adding a valid employee
    @Test
    void testAddEmployee() {
        when(roleRepository.findByName("ROLE_EMPLOYEE")).thenReturn(Optional.of(role));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.save(any())).thenReturn(user);
        when(employeeRepository.save(any())).thenReturn(employee);
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");

        Employee saved = employeeService.addEmployee(employee, "ROLE_EMPLOYEE", 1L);
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
    }

    // Test adding a manager when one already exists
    @Test
    void testAddEmployee_ManagerAlreadyExists() {
        department.setManager(user);
        Role managerRole = new Role();
        managerRole.setName("ROLE_MANAGER");
        when(roleRepository.findByName("ROLE_MANAGER")).thenReturn(Optional.of(managerRole));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() ->
            employeeService.addEmployee(employee, "ROLE_MANAGER", 1L)
        ).isInstanceOf(ManagerAlreadyExistsException.class);
    }

    // Test fetching all employees
    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAllEmployeesWithDepartment()).thenReturn(List.of(employee));
        List<Employee> list = employeeService.getAllEmployees();
        assertThat(list).hasSize(1);
    }

    // Test fetching employees for a specific manager
    @Test
    void testGetEmployeesForManager() {
        when(employeeRepository.findEmployeesByManager("manager")).thenReturn(List.of(employee));
        List<Employee> list = employeeService.getEmployeesForManager("manager");
        assertThat(list).hasSize(1);
    }

    // Test getting employee by valid ID
    @Test
    void testGetEmployeeById_Valid() {
        when(employeeRepository.findEmployeeWithDepartment(1L)).thenReturn(Optional.of(employee));
        Optional<Employee> result = employeeService.getEmployeeById(1L);
        assertThat(result).isPresent();
    }

    // Test getting employee by invalid ID
    @Test
    void testGetEmployeeById_Invalid() {
        when(employeeRepository.findEmployeeWithDepartment(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
            .isInstanceOf(EmployeeNotFoundException.class);
    }

    // Test getting employee by ID and manager username
    @Test
    void testGetEmployeeByIdForManager_Valid() {
        when(employeeRepository.findEmployeeByIdForManager(1L, "manager")).thenReturn(Optional.of(employee));
        Optional<Employee> result = employeeService.getEmployeeByIdForManager(1L, "manager");
        assertThat(result).isPresent();
    }

    // Test updating an employee
    @Test
    void testUpdateEmployee() {
        Employee updated = new Employee();
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setDepartment(department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(employeeRepository.save(any())).thenReturn(employee);

        Employee result = employeeService.updateEmployee(1L, updated);
        assertThat(result).isNotNull();
    }

    // Test deleting a regular employee
    @Test
    void testDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByManager(user)).thenReturn(null);
        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
        verify(userRepository).deleteById(user.getId());
    }

    // Test deleting a manager and unassigning them from department
    @Test
    void testDeleteEmployee_Manager() {
        department.setManager(user);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByManager(user)).thenReturn(department);
        employeeService.deleteEmployee(1L);

        verify(departmentRepository).save(department);
    }

    // Test searching employees by department name
    @Test
    void testSearchByDepartment() {
        when(employeeRepository.findByDepartment_Name("IT")).thenReturn(List.of(employee));
        assertThat(employeeService.searchByDepartment("IT")).hasSize(1);
    }

    // Test searching employees by job title
    @Test
    void testSearchByJobTitle() {
        when(employeeRepository.findByJobTitle("Engineer")).thenReturn(List.of(employee));
        assertThat(employeeService.searchByJobTitle("Engineer")).hasSize(1);
    }

    // Test advanced search for admin role
    @Test
    void testSearchEmployees_Admin() {
        EmployeeSearchRequestDTO dto = new EmployeeSearchRequestDTO();
        dto.setDepartmentId(1L);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john@example.com");

        when(userRepository.findRoleByUsername("admin")).thenReturn("ROLE_ADMIN");
        when(departmentRepository.existsById(any())).thenReturn(true);
        when(employeeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
            .thenReturn(List.of(employee));

        List<Employee> result = employeeService.searchEmployees(dto, "admin");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
    }

    // Test advanced search for manager role
    @Test
    void testSearchEmployees_Manager() {
        EmployeeSearchRequestDTO dto = new EmployeeSearchRequestDTO();
        when(userRepository.findRoleByUsername("manager")).thenReturn("ROLE_MANAGER");
        when(departmentRepository.findByManagerUsername("manager")).thenReturn(department);
        when(employeeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
            .thenReturn(List.of(employee));

        assertThat(employeeService.searchEmployees(dto, "manager")).hasSize(1);
    }

    // Test access denied for regular employee in search
    @Test
    void testSearchEmployees_AccessDenied() {
        EmployeeSearchRequestDTO dto = new EmployeeSearchRequestDTO();
        when(userRepository.findRoleByUsername("employee")).thenReturn("ROLE_EMPLOYEE");

        assertThatThrownBy(() -> employeeService.searchEmployees(dto, "employee"))
            .isInstanceOf(AccessDeniedException.class);
    }

    // Test fetching user role by username
    @Test
    void testGetUserRole() {
        when(userRepository.findRoleByUsername("admin")).thenReturn("ROLE_ADMIN");
        String role = employeeService.getUserRole("admin");
        assertThat(role).isEqualTo("ROLE_ADMIN");
    }

    // Test update employee when department is null
    @Test
    void testUpdateEmployee_DepartmentIsNull() {
        Employee updated = new Employee();
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setDepartment(null);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);

        Employee result = employeeService.updateEmployee(1L, updated);
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("New");
    }

    // Test adding employee with invalid role
    @Test
    void testAddEmployee_InvalidRole() {
        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            employeeService.addEmployee(employee, "INVALID_ROLE", 1L)
        ).isInstanceOf(RoleNotFoundException.class);
    }

    // Test adding employee with invalid department ID
    @Test
    void testAddEmployee_InvalidDepartment() {
        when(roleRepository.findByName("ROLE_EMPLOYEE")).thenReturn(Optional.of(role));
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            employeeService.addEmployee(employee, "ROLE_EMPLOYEE", 999L)
        ).isInstanceOf(EmployeeNotFoundException.class);
    }

}
