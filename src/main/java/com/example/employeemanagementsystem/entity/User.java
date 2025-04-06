package com.example.employeemanagementsystem.entity;

import jakarta.persistence.*;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity representing a User in the Employee Management System.
 * Each user can have one or more roles that determine their access permissions.
 */
@Entity
public class User {

    /**
     * Primary key: Unique identifier for each user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username for login. Must be unique and not null.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Encrypted password. Stored securely.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Many-to-many relationship between users and roles.
     * A user can have multiple roles, and each role can be assigned to multiple users.
     * Fetched eagerly to ensure roles are available upon authentication.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles;

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * @return Unique user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID.
     * 
     * @param id User ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username Username string
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's encrypted password.
     * 
     * @param password Password string
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Set of roles assigned to the user
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the user's roles.
     * 
     * @param roles Set of Role entities
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
