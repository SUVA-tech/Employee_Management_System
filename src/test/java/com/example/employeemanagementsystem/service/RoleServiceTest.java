package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.entity.Role;
import com.example.employeemanagementsystem.exception.RoleServiceException;
import com.example.employeemanagementsystem.repository.RoleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ADMIN");
    }

    // ----------------------------
    // findByName()
    // ----------------------------

    @Test
    void testFindByName_Success() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(testRole));
        Optional<Role> result = roleService.findByName("ADMIN");

        assertThat(result).isPresent().contains(testRole);
    }

    @Test
    void testFindByName_Exception() {
        when(roleRepository.findByName("ADMIN")).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> roleService.findByName("ADMIN"))
            .isInstanceOf(RoleServiceException.class)
            .hasMessageContaining("Failed to fetch role details");
    }

    // ----------------------------
    // saveRole()
    // ----------------------------

    @Test
    void testSaveRole_Success() {
        when(roleRepository.save(testRole)).thenReturn(testRole);
        Role saved = roleService.saveRole(testRole);

        assertThat(saved).isEqualTo(testRole);
    }

    @Test
    void testSaveRole_Exception() {
        when(roleRepository.save(testRole)).thenThrow(new RuntimeException("Insert failed"));

        assertThatThrownBy(() -> roleService.saveRole(testRole))
            .isInstanceOf(RoleServiceException.class)
            .hasMessageContaining("Failed to save role");
    }
}
