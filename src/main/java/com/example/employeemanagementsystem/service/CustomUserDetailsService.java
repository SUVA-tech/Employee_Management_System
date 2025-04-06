package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.entity.User;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import com.example.employeemanagementsystem.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Check if the given user is authorized to access the employee with the provided ID.
     * Admins and Managers can access any; employees can only access their own record.
     */
    public boolean isAuthorizedEmployee(String username, Long requestedId) {
        logger.info("Checking if user '{}' is authorized to access employee ID: {}", username, requestedId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        boolean isAdminOrManager = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN") || role.getName().equalsIgnoreCase("MANAGER"));

        if (isAdminOrManager) {
            return true;
        }

        Optional<Employee> employee = employeeRepository.findByUser(user);
        return employee.map(emp -> emp.getId().equals(requestedId)).orElse(false);
    }

    /**
     * Check if the given username belongs to a manager.
     */
    public boolean isManagerOfDepartment(String username) {
        logger.info("Checking if user '{}' is a manager", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        boolean isManager = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("MANAGER"));

        return isManager && employeeRepository.findByUser(user).isPresent();
    }

    /**
     * Loads user details used by Spring Security for authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getName().toUpperCase())
                        .collect(Collectors.toList())
                        .toArray(new String[0]))
                .build();
    }
}
