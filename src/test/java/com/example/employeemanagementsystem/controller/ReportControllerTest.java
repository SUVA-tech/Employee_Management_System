package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.EmployeeReportDTO;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReportController reportController;

    private EmployeeReportDTO sampleReport;

    // Setup method to initialize mocks and create a sample report DTO
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleReport = new EmployeeReportDTO();
        sampleReport.setLabel("Engineering");
        sampleReport.setCount(10L);
        sampleReport.setAverageSalary(50000.0);
    }

    // Test: should return total number of employees
    @Test
    void testGetTotalEmployees() {
        when(reportService.getTotalEmployees()).thenReturn(25L);

        ResponseEntity<Long> response = reportController.getTotalEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(25L, response.getBody());
        verify(reportService).getTotalEmployees();
    }

    // Test: Admin user should receive department-wise employee report
    @Test
    void testGetEmployeesByDepartment_Admin() {
        when(authentication.getName()).thenReturn("admin@example.com");
        when(employeeService.getUserRole("admin@example.com")).thenReturn("ROLE_ADMIN");
        when(reportService.getEmployeesByDepartment(null, "ADMIN")).thenReturn(List.of(sampleReport));

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getEmployeesByDepartment(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    // Test: Manager user should receive department-wise employee report scoped to their data
    @Test
    void testGetEmployeesByDepartment_Manager() {
        when(authentication.getName()).thenReturn("manager@example.com");
        when(employeeService.getUserRole("manager@example.com")).thenReturn("ROLE_MANAGER");
        when(reportService.getEmployeesByDepartment("manager@example.com", "MANAGER")).thenReturn(List.of(sampleReport));

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getEmployeesByDepartment(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Engineering", response.getBody().get(0).getLabel());
    }

    // Test: Unauthorized (employee) user should receive 403 Forbidden
    @Test
    void testGetEmployeesByDepartment_Unauthorized() {
        when(authentication.getName()).thenReturn("employee@example.com");
        when(employeeService.getUserRole("employee@example.com")).thenReturn("ROLE_EMPLOYEE");

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getEmployeesByDepartment(authentication);

        assertEquals(403, response.getStatusCodeValue());
    }

    // Test: should return employee count grouped by job title
    @Test
    void testGetEmployeesByJobTitle() {
        when(reportService.getEmployeesByJobTitle()).thenReturn(List.of(sampleReport));

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getEmployeesByJobTitle();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Engineering", response.getBody().get(0).getLabel());
    }

    // Test: should return employee count grouped by gender
    @Test
    void testGetEmployeesByGender() {
        when(reportService.getEmployeesByGender()).thenReturn(List.of(sampleReport));

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getEmployeesByGender();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10L, response.getBody().get(0).getCount());
    }

    // Test: Admin user should receive total salary per department
    @Test
    void testGetTotalSalaryByDepartment_Admin() {
        when(authentication.getName()).thenReturn("admin@example.com");
        when(employeeService.getUserRole("admin@example.com")).thenReturn("ROLE_ADMIN");
        when(reportService.getTotalSalaryByDepartment(null, "ADMIN")).thenReturn(List.of(sampleReport));

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getTotalSalaryByDepartment(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Engineering", response.getBody().get(0).getLabel());
    }

    // Test: Manager user should receive scoped total salary per department
    @Test
    void testGetTotalSalaryByDepartment_Manager() {
        when(authentication.getName()).thenReturn("manager@example.com");
        when(employeeService.getUserRole("manager@example.com")).thenReturn("ROLE_MANAGER");
        when(reportService.getTotalSalaryByDepartment("manager@example.com", "MANAGER")).thenReturn(List.of(sampleReport));

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getTotalSalaryByDepartment(authentication);

        assertEquals(200, response.getStatusCodeValue());
    }

    // Test: Unauthorized (employee) user should not receive salary data
    @Test
    void testGetTotalSalaryByDepartment_Unauthorized() {
        when(authentication.getName()).thenReturn("employee@example.com");
        when(employeeService.getUserRole("employee@example.com")).thenReturn("ROLE_EMPLOYEE");

        ResponseEntity<List<EmployeeReportDTO>> response = reportController.getTotalSalaryByDepartment(authentication);

        assertEquals(403, response.getStatusCodeValue());
    }
}
