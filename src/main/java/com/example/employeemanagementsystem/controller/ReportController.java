package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.EmployeeReportDTO;
import com.example.employeemanagementsystem.service.ReportService;
import com.example.employeemanagementsystem.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling API requests related to employee reports.
 */
@RestController
@RequestMapping("api/reports")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;
    private final EmployeeService employeeService;

    /**
     * Constructor-based injection for required services.
     *
     * @param reportService     the report service
     * @param employeeService   the employee service
     */
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    /**
     * Get total number of employees.
     *
     * @return total employee count as a ResponseEntity
     */
    @GetMapping("/total-employees")
    public ResponseEntity<Long> getTotalEmployees() {
        logger.info("Request to get total number of employees");
        try {
            return ResponseEntity.ok(reportService.getTotalEmployees());
        } catch (Exception e) {
            logger.error("Failed to fetch total employee count", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get number of employees grouped by department.
     * Only accessible by users with ADMIN or MANAGER roles.
     *
     * @param authentication the authentication object containing user credentials
     * @return list of employee report DTOs grouped by department
     */
    @GetMapping("/employees-by-department")
    public ResponseEntity<List<EmployeeReportDTO>> getEmployeesByDepartment(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Request to get employees by department from user: {}", username);

        try {
            String role = employeeService.getUserRole(username);

            if (role.equals("ROLE_MANAGER")) {
                return ResponseEntity.ok(reportService.getEmployeesByDepartment(username, "MANAGER"));
            } else if (role.equals("ROLE_ADMIN")) {
                return ResponseEntity.ok(reportService.getEmployeesByDepartment(null, "ADMIN"));
            }

            logger.warn("Access denied for employees trying to fetch department report: {}", username);
            return ResponseEntity.status(403).build();

        } catch (Exception e) {
            logger.error("Failed to fetch employees by department for user: {}", username, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get number of employees grouped by job title.
     *
     * @return list of employee report DTOs grouped by job title
     */
    @GetMapping("/employees-by-job-title")
    public ResponseEntity<List<EmployeeReportDTO>> getEmployeesByJobTitle() {
        logger.info("Request to get employees by job title");
        try {
            return ResponseEntity.ok(reportService.getEmployeesByJobTitle());
        } catch (Exception e) {
            logger.error("Failed to fetch employees by job title", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get number of employees grouped by gender.
     *
     * @return list of employee report DTOs grouped by gender
     */
    @GetMapping("/employees-by-gender")
    public ResponseEntity<List<EmployeeReportDTO>> getEmployeesByGender() {
        logger.info("Request to get employees by gender");
        try {
            return ResponseEntity.ok(reportService.getEmployeesByGender());
        } catch (Exception e) {
            logger.error("Failed to fetch employees by gender", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get total salary grouped by department.
     * Only accessible by users with ADMIN or MANAGER roles.
     *
     * @param authentication the authentication object containing user credentials
     * @return list of employee report DTOs representing salary totals
     */
    @GetMapping("/total-salary-by-department")
    public ResponseEntity<List<EmployeeReportDTO>> getTotalSalaryByDepartment(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Request to get total salary by department from user: {}", username);

        try {
            String role = employeeService.getUserRole(username);

            if (role.equals("ROLE_MANAGER")) {
                return ResponseEntity.ok(reportService.getTotalSalaryByDepartment(username, "MANAGER"));
            } else if (role.equals("ROLE_ADMIN")) {
                return ResponseEntity.ok(reportService.getTotalSalaryByDepartment(null, "ADMIN"));
            }

            logger.warn("Access denied for employees trying to fetch salary report: {}", username);
            return ResponseEntity.status(403).build();

        } catch (Exception e) {
            logger.error("Failed to fetch salary by department for user: {}", username, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
