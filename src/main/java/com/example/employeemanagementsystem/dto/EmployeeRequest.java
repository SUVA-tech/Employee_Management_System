package com.example.employeemanagementsystem.dto;

import com.example.employeemanagementsystem.entity.Department;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.sql.Date;

/**
 * DTO for creating or updating an Employee.
 * Includes validation constraints to ensure input correctness.
 */
public class EmployeeRequest {

    /**
     * Employee's first name. Must not be blank.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * Employee's last name. Must not be blank.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * Employee's email address. Must not be blank and should follow email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Employee's phone number. Must not be blank.
     */
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    /**
     * Employee's job title. Must not be blank.
     */
    @NotBlank(message = "Job title is required")
    private String jobTitle;

    /**
     * Employee's salary. Must be positive and not null.
     */
    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be a positive number")
    private Double salary;

    /**
     * Date when the employee was hired. Must not be null.
     */
    @NotNull(message = "Hire date is required")
    private Date hireDate;

    /**
     * Employee's date of birth. Must be in the past.
     */
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    /**
     * Employee's gender. Must not be blank.
     */
    @NotBlank(message = "Gender is required")
    private String gender;

    /**
     * Role of the employee (e.g., ADMIN, MANAGER, EMPLOYEE). Must not be blank.
     */
    @NotBlank(message = "Role is required")
    private String role;

    /**
     * Department to which the employee belongs. Must not be null and should be valid.
     */
    @NotNull(message = "Department is required")
    @Valid
    private Department department;

    // Getters and Setters

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Double getSalary() {
        return salary;
    }
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }
}
