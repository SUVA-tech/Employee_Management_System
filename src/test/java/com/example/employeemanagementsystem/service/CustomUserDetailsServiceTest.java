package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.entity.Role;
import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User adminUser;
    private User employeeUser;
    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup roles
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        Role empRole = new Role();
        empRole.setId(2L);
        empRole.setName("EMPLOYEE");
        
        Role managerRole = new Role();
        managerRole.setId(3L);
        managerRole.setName("MANAGER");

        // Create admin user
        adminUser = new User();
        adminUser.setUsername("admin@example.com");
        adminUser.setPassword("admin123");
        adminUser.setRoles(Set.of(adminRole));

        // Create employee user
        employeeUser = new User();
        employeeUser.setUsername("employee@example.com");
        employeeUser.setPassword("emp123");
        employeeUser.setRoles(Set.of(empRole));

        // Link employee to employee user
        employee = new Employee();
        employee.setId(101L);
        employee.setUser(employeeUser);
    }

    // -----------------------------------------------------------------------
    // Test: loadUserByUsername()
    // -----------------------------------------------------------------------

    @Test
    void testLoadUserByUsername_Success() {
        // Test if user details are loaded successfully when user exists
        when(userRepository.findByUsername("admin@example.com"))
                .thenReturn(Optional.of(adminUser));

        UserDetails details = userDetailsService.loadUserByUsername("admin@example.com");

        assertThat(details.getUsername()).isEqualTo("admin@example.com");
        assertThat(details.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        // Test if UsernameNotFoundException is thrown when user does not exist
        when(userRepository.findByUsername("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with username");
    }

    // -----------------------------------------------------------------------
    // Test: isAuthorizedEmployee()
    // -----------------------------------------------------------------------

    @Test
    void testIsAuthorized_AdminAccessesAnyEmployee() {
        // Admin is allowed to access any employee's data
        when(userRepository.findByUsername("admin@example.com")).thenReturn(Optional.of(adminUser));

        boolean authorized = userDetailsService.isAuthorizedEmployee("admin@example.com", 123L);
        assertThat(authorized).isTrue();
    }

    @Test
    void testIsAuthorized_EmployeeAccessesOwnData() {
        // Employee can access their own data
        when(userRepository.findByUsername("employee@example.com")).thenReturn(Optional.of(employeeUser));
        when(employeeRepository.findByUser(employeeUser)).thenReturn(Optional.of(employee));

        boolean authorized = userDetailsService.isAuthorizedEmployee("employee@example.com", 101L);
        assertThat(authorized).isTrue();
    }

    @Test
    void testIsAuthorized_EmployeeAccessesOtherEmployee() {
        // Employee cannot access other employee’s data
        when(userRepository.findByUsername("employee@example.com")).thenReturn(Optional.of(employeeUser));
        when(employeeRepository.findByUser(employeeUser)).thenReturn(Optional.of(employee));

        boolean authorized = userDetailsService.isAuthorizedEmployee("employee@example.com", 999L);
        assertThat(authorized).isFalse();
    }

    @Test
    void testIsAuthorized_UserNotFound() {
        // Should throw exception when user is not found
        when(userRepository.findByUsername("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.isAuthorizedEmployee("ghost@example.com", 1L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    // -----------------------------------------------------------------------
    // Test: isManagerOfDepartment()
    // -----------------------------------------------------------------------

    @Test
    void testIsManagerOfDepartment_ValidManager() {
        // User with MANAGER role is correctly recognized
        User manager = new User();
        manager.setUsername("manager@example.com");

        Role managerRole = new Role();
        managerRole.setId(3L);
        managerRole.setName("MANAGER");

        manager.setRoles(Set.of(managerRole));

        when(userRepository.findByUsername("manager@example.com")).thenReturn(Optional.of(manager));
        when(employeeRepository.findByUser(manager)).thenReturn(Optional.of(new Employee()));

        boolean isManager = userDetailsService.isManagerOfDepartment("manager@example.com");
        assertThat(isManager).isTrue();
    }

    @Test
    void testIsManagerOfDepartment_NotManager() {
        // A user who is not a manager should return false
        when(userRepository.findByUsername("employee@example.com")).thenReturn(Optional.of(employeeUser));
        when(employeeRepository.findByUser(employeeUser)).thenReturn(Optional.of(employee));

        boolean result = userDetailsService.isManagerOfDepartment("employee@example.com");
        assertThat(result).isFalse();
    }

    @Test
    void testIsManagerOfDepartment_UserNotFound() {
        // Should throw exception if user is not found
        when(userRepository.findByUsername("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.isManagerOfDepartment("ghost@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
