package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.EmployeeRequest;
import com.example.employeemanagementsystem.dto.EmployeeSearchRequestDTO;
import com.example.employeemanagementsystem.entity.Department;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.exception.AccessDeniedException;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.exception.ManagerAlreadyExistsException;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.UserAlreadyExistsException;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeController employeeController;
    
    private EmployeeSearchRequestDTO searchRequest;
   
    private List<Employee> sampleEmployees;
    private Employee sampleEmployee;
    private EmployeeRequest employeeRequest;
    private Employee savedEmployee;
    private Employee updatedEmployee; // for update test

    private Date toDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }
    
    private java.sql.Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day); // Month is 0-based
        return new java.sql.Date(cal.getTimeInMillis()); // convert util.Date to sql.Date
    }



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup for getEmployeeById
        sampleEmployee = new Employee();
        sampleEmployee.setId(1L);
        sampleEmployee.setFirstName("John");
        sampleEmployee.setLastName("Doe");
        sampleEmployee.setEmail("john.doe@example.com");

        // Setup for addEmployee
        employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("Jane");
        employeeRequest.setLastName("Doe");
        employeeRequest.setEmail("jane.doe@example.com");
        employeeRequest.setPhoneNumber("9876543210");
        employeeRequest.setJobTitle("Manager");
        employeeRequest.setSalary(85000.0);
        employeeRequest.setHireDate(toDate(LocalDate.now().minusDays(30)));
        employeeRequest.setDateOfBirth(toDate(LocalDate.of(1990, 5, 15)));
        employeeRequest.setGender("Female");
        employeeRequest.setRole("ROLE_MANAGER");

        Department department = new Department();
        department.setId(101L);
        department.setName("Engineering");
        employeeRequest.setDepartment(department);

        savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setEmail(employeeRequest.getEmail());

        // Setup for updateEmployee
        updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setFirstName("Johnny");
        updatedEmployee.setLastName("Doe");
        updatedEmployee.setEmail("johnny.doe@example.com");
        updatedEmployee.setPhoneNumber("1234567890");
        updatedEmployee.setJobTitle("Senior Manager");
        updatedEmployee.setSalary(95000.0);
        updatedEmployee.setHireDate(toDate(LocalDate.now().minusDays(10)));
        updatedEmployee.setDateOfBirth(toDate(LocalDate.of(1988, 3, 20)));
        updatedEmployee.setDepartment(department);
        
        //Setup for search and filter
        searchRequest = new EmployeeSearchRequestDTO();
        searchRequest.setName("john");
        searchRequest.setDepartmentId(1L);
        searchRequest.setJobTitle("Developer");
        searchRequest.setGender("Male");

        Employee emp1 = new Employee();
        emp1.setId(1L);
        emp1.setFirstName("John");
        emp1.setLastName("Doe");

        sampleEmployees = List.of(emp1);
    }
    
    //Test for getEmployees

    @Test
    void getEmployees_AdminRole_ReturnsAllEmployees() {
        String username = "admin@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_ADMIN");
        when(employeeService.getAllEmployees()).thenReturn(List.of(sampleEmployee));

        ResponseEntity<?> response = employeeController.getEmployees(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);
        verify(employeeService).getUserRole(username);
        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployees_ManagerRole_ReturnsEmployeesByDepartment() {
        String username = "manager@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_MANAGER");
        when(employeeService.getEmployeesForManager(username)).thenReturn(List.of(sampleEmployee));

        ResponseEntity<?> response = employeeController.getEmployees(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);
        verify(employeeService).getEmployeesForManager(username);
    }

    @Test
    void getEmployees_UserRole_ReturnsForbidden() {
        String username = "user@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_USER");

        ResponseEntity<?> response = employeeController.getEmployees(authentication);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals(Map.of("error", "Access denied: unauthorized role for user " + username), response.getBody());
    }


    @Test
    void getEmployees_UnknownRole_ReturnsForbidden() {
        String username = "unknown@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_UNKNOWN");

        ResponseEntity<?> response = employeeController.getEmployees(authentication);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals(Map.of("error", "Access denied: unauthorized role for user " + username), response.getBody());
    }

    @Test
    void getEmployees_ServiceException_ReturnsInternalServerError() {
        String username = "admin@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenThrow(new RuntimeException("Unexpected Error"));

        ResponseEntity<?> response = employeeController.getEmployees(authentication);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals(Map.of("error", "Internal server error while fetching employees"), response.getBody());
    }
    
    //Test for GetEmployeeByID
    
    @Test
    public void getEmployeeById_AdminRole_ReturnsEmployee() {
        Long employeeId = 1L;
        String username = "admin@example.com";
        Employee employee = new Employee();
        employee.setId(employeeId);

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_ADMIN");
        when(employeeService.getEmployeeById(employeeId)).thenReturn(Optional.of(employee));

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee, response.getBody());
        verify(employeeService).getEmployeeById(employeeId);
    }

    @Test
    public void getEmployeeById_ManagerRole_ReturnsEmployee() {
        Long employeeId = 2L;
        String username = "manager@example.com";
        Employee employee = new Employee();
        employee.setId(employeeId);

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_MANAGER");
        when(employeeService.getEmployeeByIdForManager(employeeId, username)).thenReturn(Optional.of(employee));

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee, response.getBody());
        verify(employeeService).getEmployeeByIdForManager(employeeId, username);
    }

    @Test
    public void getEmployeeById_UnauthorizedRole_ReturnsForbidden() {
        Long employeeId = 3L;
        String username = "user@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_EMPLOYEE");

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(employeeService, never()).getEmployeeById(anyLong());
        verify(employeeService, never()).getEmployeeByIdForManager(anyLong(), anyString());
    }

    @Test
    public void getEmployeeById_EmployeeNotFound_ThrowsException() {
        Long employeeId = 4L;
        String username = "admin@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_ADMIN");
        when(employeeService.getEmployeeById(employeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeController.getEmployeeById(employeeId, authentication));
    }

    @Test
    public void getEmployeeById_ServiceThrowsUnexpectedError_ReturnsInternalServerError() {
        Long employeeId = 5L;
        String username = "admin@example.com";

        when(authentication.getName()).thenReturn(username);
        when(employeeService.getUserRole(username)).thenReturn("ROLE_ADMIN");
        when(employeeService.getEmployeeById(employeeId)).thenThrow(new RuntimeException("Unexpected Error"));

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
    //test for addEmployee

    @Test
    void addEmployee_ValidRequest_ReturnsCreated() {
        when(employeeService.addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L)))
                .thenReturn(savedEmployee);

        ResponseEntity<Employee> response = employeeController.addEmployee(employeeRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedEmployee, response.getBody());
        verify(employeeService).addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L));
    }

    @Test
    void addEmployee_UserAlreadyExistsException_ThrowsHandled() {
        when(employeeService.addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L)))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        assertThrows(UserAlreadyExistsException.class,
                () -> employeeController.addEmployee(employeeRequest));

        verify(employeeService).addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L));
    }

    @Test
    void addEmployee_ResourceNotFoundException_ThrowsHandled() {
        when(employeeService.addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L)))
                .thenThrow(new ResourceNotFoundException("Department not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> employeeController.addEmployee(employeeRequest));
    }

    @Test
    void addEmployee_ManagerAlreadyExistsException_ThrowsHandled() {
        when(employeeService.addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L)))
                .thenThrow(new ManagerAlreadyExistsException("Manager already exists"));

        assertThrows(ManagerAlreadyExistsException.class,
                () -> employeeController.addEmployee(employeeRequest));
    }

    @Test
    void addEmployee_UnexpectedError_ReturnsInternalServerError() {
        when(employeeService.addEmployee(any(Employee.class), eq("ROLE_MANAGER"), eq(101L)))
                .thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<Employee> response = employeeController.addEmployee(employeeRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    //test for update employee
    
    @Test
    void updateEmployee_Success_ReturnsUpdatedEmployee() {
        Long employeeId = 1L;

        when(employeeService.updateEmployee(employeeId, updatedEmployee)).thenReturn(updatedEmployee);

        ResponseEntity<Employee> response = employeeController.updateEmployee(employeeId, updatedEmployee);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedEmployee, response.getBody());
        verify(employeeService).updateEmployee(employeeId, updatedEmployee);
    }

    @Test
    void updateEmployee_EmployeeNotFound_ReturnsNotFound() {
        Long employeeId = 100L;

        when(employeeService.updateEmployee(employeeId, updatedEmployee))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: " + employeeId));

        ResponseEntity<Employee> response = employeeController.updateEmployee(employeeId, updatedEmployee);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(employeeService).updateEmployee(employeeId, updatedEmployee);
    }

    @Test
    void updateEmployee_InternalServerError_ReturnsInternalServerError() {
        Long employeeId = 1L;

        when(employeeService.updateEmployee(employeeId, updatedEmployee))
                .thenThrow(new RuntimeException("Unexpected Error"));

        ResponseEntity<Employee> response = employeeController.updateEmployee(employeeId, updatedEmployee);

        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(employeeService).updateEmployee(employeeId, updatedEmployee);
    }
    
    //test for deleteEmployee
    @Test
    void testDeleteEmployee_Success() {
        Long employeeId = 1L;

        // No exception thrown by service means success
        doNothing().when(employeeService).deleteEmployee(employeeId);

        ResponseEntity<Void> response = employeeController.deleteEmployee(employeeId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }

    @Test
    void testDeleteEmployee_EmployeeNotFound() {
        Long employeeId = 999L;

        doThrow(new EmployeeNotFoundException("Employee not found")).when(employeeService).deleteEmployee(employeeId);

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.deleteEmployee(employeeId);
        });

        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }

    //test to search and filter
    
    @Test
    void testSearchEmployees_AsAdmin_Success() {
        when(authentication.getName()).thenReturn("adminUser");
        when(employeeService.searchEmployees(searchRequest, "adminUser")).thenReturn(sampleEmployees);

        ResponseEntity<List<Employee>> response = employeeController.searchEmployees(searchRequest, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());

        verify(employeeService).searchEmployees(searchRequest, "adminUser");
    }

    @Test
    void testSearchEmployees_AsManager_Success() {
        when(authentication.getName()).thenReturn("managerUser");
        when(employeeService.searchEmployees(searchRequest, "managerUser")).thenReturn(sampleEmployees);

        ResponseEntity<List<Employee>> response = employeeController.searchEmployees(searchRequest, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());

        verify(employeeService).searchEmployees(searchRequest, "managerUser");
    }

    @Test
    void testSearchEmployees_AccessDenied() {
        when(authentication.getName()).thenReturn("basicUser");
        when(employeeService.searchEmployees(searchRequest, "basicUser"))
                .thenThrow(new AccessDeniedException("Access Denied"));

        ResponseEntity<List<Employee>> response = employeeController.searchEmployees(searchRequest, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(employeeService).searchEmployees(searchRequest, "basicUser");
    }

    @Test
    void testSearchEmployees_InternalServerError() {
        when(authentication.getName()).thenReturn("adminUser");
        when(employeeService.searchEmployees(searchRequest, "adminUser"))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<Employee>> response = employeeController.searchEmployees(searchRequest, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(employeeService).searchEmployees(searchRequest, "adminUser");
    }
    
    @Test
    void testSearchEmployees_AsAdmin_EmptyResults() {
        when(authentication.getName()).thenReturn("adminUser");
        when(employeeService.searchEmployees(searchRequest, "adminUser")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Employee>> response = employeeController.searchEmployees(searchRequest, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(employeeService).searchEmployees(searchRequest, "adminUser");
    }

    @Test
    void testSearchEmployees_AsManager_EmptyResults() {
        when(authentication.getName()).thenReturn("managerUser");
        when(employeeService.searchEmployees(searchRequest, "managerUser")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Employee>> response = employeeController.searchEmployees(searchRequest, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(employeeService).searchEmployees(searchRequest, "managerUser");
    }
    
 
    
}