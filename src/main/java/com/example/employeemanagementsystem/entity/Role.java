package com.example.employeemanagementsystem.entity;

import org.springframework.security.core.GrantedAuthority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

/**
 * Entity representing a Role in the system.
 * Implements Spring Security's GrantedAuthority interface for role-based access control (RBAC).
 */
@Entity
public class Role implements GrantedAuthority {

    /**
     * Primary key: Unique identifier for each role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the role (e.g., ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE).
     * Must be unique and non-null.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Many-to-many relationship with the User entity.
     * A role can be assigned to multiple users.
     */
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users;

    /**
     * Default constructor.
     */
    public Role() {}

    /**
     * Constructor with role name.
     * 
     * @param name Name of the role
     */
    public Role(String name) {
        super();
        this.name = name;
    }

    /**
     * Returns the authority granted by this role (used by Spring Security).
     * 
     * @return Role name as authority string
     */
    @Override
    public String getAuthority() {
        return name;
    }

    /**
     * @return Role ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the role ID.
     * 
     * @param id Role ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Role name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the role name.
     * 
     * @param name Role name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Users assigned to this role
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * Sets the users for this role.
     * 
     * @param users Set of users
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
