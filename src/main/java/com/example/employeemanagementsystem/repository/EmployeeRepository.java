package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.dto.*;
import com.example.employeemanagementsystem.entity.Department;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    // Find employees by department name
    List<Employee> findByDepartment_Name(String departmentName);

    // Find employees by job title
    List<Employee> findByJobTitle(String jobTitle);

    // Find an employee by email
    Optional<Employee> findByEmail(String email);

    // Find an employee by associated user
    Optional<Employee> findByUser(User user);

    // Get total count of employees
    @Query("SELECT COUNT(e) FROM Employee e")
    Long getTotalEmployees();

    // Manager: Get employee report grouped by department for manager's own department
    @Query("SELECT new com.example.employeemanagementsystem.dto.EmployeeReportDTO(e.department.name, COUNT(e), AVG(e.salary), SUM(e.salary)) FROM Employee e WHERE e.department.manager.username = :managerUsername GROUP BY e.department.name")
    List<EmployeeReportDTO> getEmployeesByDepartmentForManager(@Param("managerUsername") String managerUsername);

    // Admin: Get global employee report grouped by department
    @Query("SELECT new com.example.employeemanagementsystem.dto.EmployeeReportDTO(e.department.name, COUNT(e), AVG(e.salary), SUM(e.salary)) FROM Employee e GROUP BY e.department.name")
    List<EmployeeReportDTO> getEmployeesByDepartment();

    // Get employee report grouped by job title
    @Query("SELECT new com.example.employeemanagementsystem.dto.EmployeeReportDTO(e.jobTitle, COUNT(e), AVG(e.salary), SUM(e.salary)) FROM Employee e GROUP BY e.jobTitle")
    List<EmployeeReportDTO> getEmployeesByJobTitle();

    // Get employee report grouped by gender
    @Query("SELECT new com.example.employeemanagementsystem.dto.EmployeeReportDTO(e.gender, COUNT(e), COALESCE(AVG(e.salary), 0), SUM(e.salary)) FROM Employee e GROUP BY e.gender")
    List<EmployeeReportDTO> getEmployeesByGender();

    // Manager: Get total salary report grouped by department for manager's own department
    @Query("SELECT new com.example.employeemanagementsystem.dto.EmployeeReportDTO(e.department.name, COUNT(e), AVG(e.salary), SUM(e.salary)) FROM Employee e WHERE e.department.manager.username = :managerUsername GROUP BY e.department.name")
    List<EmployeeReportDTO> getTotalSalaryByDepartmentForManager(@Param("managerUsername") String managerUsername);

    // Admin: Get global total salary report grouped by department
    @Query("SELECT new com.example.employeemanagementsystem.dto.EmployeeReportDTO(e.department.name, COUNT(e), AVG(e.salary), SUM(e.salary)) FROM Employee e GROUP BY e.department.name")
    List<EmployeeReportDTO> getTotalSalaryByDepartment();

    // Get all employees for a specific manager by manager's username
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.department.manager.username = :managerUsername")
    List<Employee> findEmployeesByManager(@Param("managerUsername") String managerUsername);

    // Get all employees with department details
    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllEmployeesWithDepartment();

    // Get employee by ID with department details
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :employeeId")
    Optional<Employee> findEmployeeWithDepartment(@Param("employeeId") Long employeeId);

    // Manager: Get employee by ID within the manager's department
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id AND e.department.manager.username = :managerUsername")
    Optional<Employee> findEmployeeByIdForManager(@Param("id") Long id, @Param("managerUsername") String managerUsername);
}
