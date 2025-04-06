package com.example.employeemanagementsystem.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO used for searching employees based on filters like name, department, job title, and gender.
 */
public class EmployeeSearchRequestDTO {

    /**
     * Name of the employee (partial or full match). Max 50 characters.
     */
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    /**
     * ID of the department to filter employees by.
     */
    private Long departmentId;

    /**
     * Job title of the employee. Max 50 characters.
     */
    @Size(max = 50, message = "Job title must not exceed 50 characters")
    private String jobTitle;

    /**
     * Gender of the employee. Should be 'MALE' or 'FEMALE'. Max 10 characters.
     */
    @Size(max = 10, message = "Gender should be either MALE or FEMALE")
    private String gender;

    // Getters and Setters

    /**
     * Gets the employee name filter.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the employee name filter.
     * @param name employee name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the department ID filter.
     * @return departmentId
     */
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the department ID filter.
     * @param departmentId department ID
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Gets the job title filter.
     * @return jobTitle
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the job title filter.
     * @param jobTitle job title
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Gets the gender filter.
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender filter.
     * @param gender gender (MALE/FEMALE)
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "EmployeeSearchRequestDTO [name=" + name + ", departmentId=" + departmentId +
                ", jobTitle=" + jobTitle + ", gender=" + gender + "]";
    }
}
