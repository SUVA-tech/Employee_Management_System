package com.example.employeemanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity representing a Department in the Employee Management System.
 * Each department has a unique ID, name, and an optional manager (User).
 */
@Entity
public class Department {

    /**
     * Unique identifier for the department.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique and non-null name of the department.
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Manager of the department (one-to-one relationship).
     * Ignored during JSON serialization to prevent circular references.
     */
    @OneToOne
    @JoinColumn(name = "manager_id", unique = true)
    @JsonIgnore
    private User manager;

    // Constructors

    /**
     * Default no-args constructor.
     */
    public Department() {}

    /**
     * Parameterized constructor for department entity.
     * @param id department ID
     * @param name department name
     * @param manager manager user entity
     */
    public Department(Long id, String name, User manager) {
        this.id = id;
        this.name = name;
        this.manager = manager;
    }

    // Getters and setters

    /**
     * Gets the department ID.
     * @return department ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the department ID.
     * @param id department ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the department.
     * @return department name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the department name.
     * @param name department name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the manager assigned to the department.
     * @return manager (User)
     */
    public User getManager() {
        return manager;
    }

    /**
     * Sets the manager for the department.
     * @param manager user who manages this department
     */
    public void setManager(User manager) {
        this.manager = manager;
    }

    /**
     * Checks if the department has a manager assigned.
     * @return true if manager is present, false otherwise
     */
    public boolean hasManager() {
        return this.manager != null;
    }

    @Override
    public String toString() {
        return "Department [id=" + id + ", name=" + name + ", manager=" + manager + "]";
    }
}
