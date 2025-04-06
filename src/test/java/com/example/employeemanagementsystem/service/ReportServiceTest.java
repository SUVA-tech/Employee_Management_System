package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.EmployeeReportDTO;
import com.example.employeemanagementsystem.exception.ReportGenerationException;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------
    // getTotalEmployees()
    // ---------------------------------------

    @Test
    void testGetTotalEmployees_Success() {
        when(employeeRepository.getTotalEmployees()).thenReturn(10L);
        Long result = reportService.getTotalEmployees();
        assertThat(result).isEqualTo(10L);
    }

    @Test
    void testGetTotalEmployees_Exception() {
        when(employeeRepository.getTotalEmployees()).thenThrow(new RuntimeException("DB error"));
        assertThatThrownBy(() -> reportService.getTotalEmployees())
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Unable to fetch total employee count");
    }

    // ---------------------------------------
    // getEmployeesByDepartment()
    // ---------------------------------------

    @Test
    void testGetEmployeesByDepartment_AsManager() {
        List<EmployeeReportDTO> mockList = Arrays.asList(new EmployeeReportDTO());
        when(employeeRepository.getEmployeesByDepartmentForManager("manager1")).thenReturn(mockList);
        List<EmployeeReportDTO> result = reportService.getEmployeesByDepartment("manager1", "MANAGER");
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetEmployeesByDepartment_AsAdmin() {
        List<EmployeeReportDTO> mockList = Arrays.asList(new EmployeeReportDTO());
        when(employeeRepository.getEmployeesByDepartment()).thenReturn(mockList);
        List<EmployeeReportDTO> result = reportService.getEmployeesByDepartment("admin", "ADMIN");
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetEmployeesByDepartment_Exception() {
        when(employeeRepository.getEmployeesByDepartment()).thenThrow(new RuntimeException("Error"));
        assertThatThrownBy(() -> reportService.getEmployeesByDepartment("admin", "ADMIN"))
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Failed to generate department report");
    }

    // ---------------------------------------
    // getEmployeesByJobTitle()
    // ---------------------------------------

    @Test
    void testGetEmployeesByJobTitle_Success() {
        List<EmployeeReportDTO> mockList = Collections.singletonList(new EmployeeReportDTO());
        when(employeeRepository.getEmployeesByJobTitle()).thenReturn(mockList);
        List<EmployeeReportDTO> result = reportService.getEmployeesByJobTitle();
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetEmployeesByJobTitle_Exception() {
        when(employeeRepository.getEmployeesByJobTitle()).thenThrow(new RuntimeException("Fail"));
        assertThatThrownBy(() -> reportService.getEmployeesByJobTitle())
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Failed to generate job title report");
    }

    // ---------------------------------------
    // getEmployeesByGender()
    // ---------------------------------------

    @Test
    void testGetEmployeesByGender_Success() {
        List<EmployeeReportDTO> mockList = Collections.singletonList(new EmployeeReportDTO());
        when(employeeRepository.getEmployeesByGender()).thenReturn(mockList);
        List<EmployeeReportDTO> result = reportService.getEmployeesByGender();
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetEmployeesByGender_Exception() {
        when(employeeRepository.getEmployeesByGender()).thenThrow(new RuntimeException("Fail"));
        assertThatThrownBy(() -> reportService.getEmployeesByGender())
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Failed to generate gender report");
    }

    // ---------------------------------------
    // getTotalSalaryByDepartment()
    // ---------------------------------------

    @Test
    void testGetTotalSalaryByDepartment_AsManager() {
        List<EmployeeReportDTO> mockList = Arrays.asList(new EmployeeReportDTO());
        when(employeeRepository.getTotalSalaryByDepartmentForManager("manager1")).thenReturn(mockList);
        List<EmployeeReportDTO> result = reportService.getTotalSalaryByDepartment("manager1", "MANAGER");
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetTotalSalaryByDepartment_AsAdmin() {
        List<EmployeeReportDTO> mockList = Arrays.asList(new EmployeeReportDTO());
        when(employeeRepository.getTotalSalaryByDepartment()).thenReturn(mockList);
        List<EmployeeReportDTO> result = reportService.getTotalSalaryByDepartment("admin", "ADMIN");
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetTotalSalaryByDepartment_Exception() {
        when(employeeRepository.getTotalSalaryByDepartment()).thenThrow(new RuntimeException("DB issue"));
        assertThatThrownBy(() -> reportService.getTotalSalaryByDepartment("admin", "ADMIN"))
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Failed to generate salary report");
    }
}
