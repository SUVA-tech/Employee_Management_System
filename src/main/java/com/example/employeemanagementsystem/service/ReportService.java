package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.EmployeeReportDTO;
import com.example.employeemanagementsystem.exception.ReportGenerationException;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    // Logger for tracking method calls and debugging
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final EmployeeRepository employeeRepository;

    // Constant to represent Manager role
    private static final String ROLE_MANAGER = "MANAGER";

    // Constructor injection of EmployeeRepository
    public ReportService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Fetch total number of employees in the system.
     * 
     * @return total count of employees
     */
    public Long getTotalEmployees() {
        try {
            logger.info("Fetching total number of employees");
            return employeeRepository.getTotalEmployees();
        } catch (Exception e) {
            logger.error("Error while fetching total employee count", e);
            throw new ReportGenerationException("Unable to fetch total employee count");
        }
    }

    /**
     * Generate report of employees grouped by department.
     * If the user is a manager, fetch only data for their department.
     * 
     * @param managerUsername Username of the manager (if applicable)
     * @param role Role of the user (Admin or Manager)
     * @return list of department-wise employee counts
     */
    public List<EmployeeReportDTO> getEmployeesByDepartment(String managerUsername, String role) {
        try {
            logger.info("Generating department report for role: {}", role);
            if (ROLE_MANAGER.equals(role)) {
                return employeeRepository.getEmployeesByDepartmentForManager(managerUsername);
            }
            return employeeRepository.getEmployeesByDepartment();
        } catch (Exception e) {
            logger.error("Error generating department report for role: {}", role, e);
            throw new ReportGenerationException("Failed to generate department report");
        }
    }

    /**
     * Generate report of employees grouped by job title.
     * 
     * @return list of job title-wise employee counts
     */
    public List<EmployeeReportDTO> getEmployeesByJobTitle() {
        try {
            logger.info("Generating report: employees by job title");
            return employeeRepository.getEmployeesByJobTitle();
        } catch (Exception e) {
            logger.error("Error generating job title report", e);
            throw new ReportGenerationException("Failed to generate job title report");
        }
    }

    /**
     * Generate report of employees grouped by gender.
     * 
     * @return list of gender-wise employee counts
     */
    public List<EmployeeReportDTO> getEmployeesByGender() {
        try {
            logger.info("Generating report: employees by gender");
            return employeeRepository.getEmployeesByGender();
        } catch (Exception e) {
            logger.error("Error generating gender report", e);
            throw new ReportGenerationException("Failed to generate gender report");
        }
    }

    /**
     * Generate report of total salary grouped by department.
     * If the user is a manager, fetch only salary details for their department.
     * 
     * @param managerUsername Username of the manager (if applicable)
     * @param role Role of the user (Admin or Manager)
     * @return list of department-wise total salary information
     */
    public List<EmployeeReportDTO> getTotalSalaryByDepartment(String managerUsername, String role) {
        try {
            logger.info("Generating salary report by department for role: {}", role);
            if (ROLE_MANAGER.equals(role)) {
                return employeeRepository.getTotalSalaryByDepartmentForManager(managerUsername);
            }
            return employeeRepository.getTotalSalaryByDepartment();
        } catch (Exception e) {
            logger.error("Error generating salary report for role: {}", role, e);
            throw new ReportGenerationException("Failed to generate salary report");
        }
    }
}
