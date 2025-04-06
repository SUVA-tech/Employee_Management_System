package com.example.employeemanagementsystem.dto;

/**
 * Data Transfer Object for employee-related reporting.
 * Represents aggregated employee statistics such as count,
 * average salary, and total salary based on different groupings
 * like department, job title, or gender.
 */
public class EmployeeReportDTO {

    /**
     * The label used to group data (e.g., department name, job title, or gender).
     */
    private String label;

    /**
     * The number of employees in this group.
     */
    private Long count;

    /**
     * The average salary of employees in this group.
     */
    private Double averageSalary;

    /**
     * The total salary of employees in this group (used in department reports).
     */
    private Double totalSalary;

    /**
     * Default constructor.
     */
    public EmployeeReportDTO() {
        super();
    }

    /**
     * Constructor for reports based on job title or gender.
     * Excludes total salary.
     *
     * @param label         the group label (job title or gender)
     * @param count         the number of employees
     * @param averageSalary the average salary
     */
    public EmployeeReportDTO(String label, Long count, Double averageSalary) {
        this.label = label;
        this.count = count;
        this.averageSalary = averageSalary;
    }

    /**
     * Constructor for department-based reports including total salary.
     *
     * @param label         the department name
     * @param count         the number of employees
     * @param averageSalary the average salary
     * @param totalSalary   the total salary of all employees in the department
     */
    public EmployeeReportDTO(String label, Long count, Double averageSalary, Double totalSalary) {
        this.label = label;
        this.count = count;
        this.averageSalary = averageSalary;
        this.totalSalary = totalSalary;
    }

    // Getters and Setters

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(Double averageSalary) {
        this.averageSalary = averageSalary;
    }

    public Double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(Double totalSalary) {
        this.totalSalary = totalSalary;
    }

    /**
     * Returns a string representation of the DTO.
     *
     * @return a formatted string with label, count, average salary, and optionally total salary
     */
    @Override
    public String toString() {
        if (totalSalary != null && totalSalary != 0) {
            return "EmployeeReportDTO [label=" + label + ", count=" + count +
                    ", averageSalary=" + averageSalary + ", totalSalary=" + totalSalary + "]";
        }
        return "EmployeeReportDTO [label=" + label + ", count=" + count + ", averageSalary=" + averageSalary + "]";
    }
}
