package com.example.employeemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.employeemanagementsystem.entity.Department;
import com.example.employeemanagementsystem.entity.User;

public interface DepartmentRepository extends JpaRepository<Department,Long>{
	Department findByManager(User manager);

	 // Fetch department by manager's username
    @Query("SELECT d FROM Department d WHERE d.manager.username = :username")
    Department findByManagerUsername(@Param("username") String username);
}
