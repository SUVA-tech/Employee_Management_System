package com.example.employeemanagementsystem.entity;

import jakarta.persistence.*;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Entity representing an Employee in the Employee Management System.
 * Stores employee's personal details, department, user account, and job information.
 */
@Entity
public class Employee {

    /**
     * Primary key: Unique identifier for each employee.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First name of the employee.
     */
    private String firstName;

    /**
     * Last name of the employee.
     */
    private String lastName;

    /**
     * Email address of the employee.
     */
    private String email;

    /**
     * Contact number of the employee.
     */
    private String phoneNumber;

    /**
     * Job title or designation of the employee.
     */
    private String jobTitle;

    /**
     * Monthly salary of the employee.
     */
    private Double salary;

    /**
     * Gender of the employee (e.g., MALE, FEMALE).
     */
    @Column(length = 10)
    private String gender;

    /**
     * One-to-one relationship to the User entity (login credentials).
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    @JsonIgnore
    private User user;

    /**
     * Many-to-one relationship to the Department entity.
     * An employee belongs to one department.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    /**
     * Date when the employee was hired.
     */
    @Temporal(TemporalType.DATE)
    private Date hireDate;

    /**
     * Employee's date of birth.
     */
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    /**
     * Default constructor.
     */
    public Employee() {
        super();
    }

    /**
     * Parameterized constructor to create an employee with specified fields.
     *
     * @param id          Employee ID
     * @param firstName   First name
     * @param lastName    Last name
     * @param email       Email
     * @param phoneNumber Phone number
     * @param department  Department object
     * @param jobTitle    Job title
     * @param salary      Salary
     * @param hireDate    Hire date
     * @param dateOfBirth Date of birth
     * @param gender      Gender
     */
    public Employee(Long id, String firstName, String lastName, String email, String phoneNumber,
                    Department department, String jobTitle, Double salary,
                    Date hireDate, Date dateOfBirth, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.hireDate = hireDate;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    // Getters and Setters

    /**
     * @return Employee ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Set employee ID
     */
    public void setId(Long id) {
        this.id = id;
    }

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

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
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

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee [id=" + id +
               ", firstName=" + firstName +
               ", lastName=" + lastName +
               ", email=" + email +
               ", phoneNumber=" + phoneNumber +
               ", department=" + department +
               ", jobTitle=" + jobTitle +
               ", salary=" + salary +
               ", hireDate=" + hireDate +
               ", dateOfBirth=" + dateOfBirth + "]";
    }
}
