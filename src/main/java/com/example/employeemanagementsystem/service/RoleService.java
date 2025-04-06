package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Role;
import com.example.employeemanagementsystem.exception.RoleServiceException;
import com.example.employeemanagementsystem.repository.RoleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    // Logger for debugging and auditing
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    // Constructor injection of RoleRepository
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Find a role by its name.
     * 
     * @param name name of the role (e.g., ADMIN, MANAGER)
     * @return Optional<Role> if found, empty otherwise
     * @throws RoleServiceException if any error occurs while fetching the role
     */
    public Optional<Role> findByName(String name) {
        try {
            logger.info("Searching for role by name: {}", name);
            return roleRepository.findByName(name);
        } catch (Exception e) {
            logger.error("Error occurred while searching for role: {}", name, e);
            throw new RoleServiceException("Failed to fetch role details");
        }
    }

    /**
     * Save a new role or update an existing one.
     * 
     * @param role the Role entity to be saved
     * @return the saved Role entity
     * @throws RoleServiceException if saving fails
     */
    public Role saveRole(Role role) {
        try {
            logger.info("Saving role: {} (ID: {})", role.getName(), role.getId());
            return roleRepository.save(role);
        } catch (Exception e) {
            logger.error("Error occurred while saving role: {}", role.getName(), e);
            throw new RoleServiceException("Failed to save role");
        }
    }
}
