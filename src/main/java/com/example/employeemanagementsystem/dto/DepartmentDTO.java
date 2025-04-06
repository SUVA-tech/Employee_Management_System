package com.example.employeemanagementsystem.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Department-related operations.
 * Used to encapsulate and validate department data in requests.
 */
public class DepartmentDTO {

    /**
     * The ID of the department.
     * This field is mandatory.
     */
    @NotNull(message = "Department ID is required")
    private Long id;

    /**
     * Gets the department ID.
     *
     * @return the department ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the department ID.
     *
     * @param id the department ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
}
