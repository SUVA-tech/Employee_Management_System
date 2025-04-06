package com.example.employeemanagementsystem.repository;

import com.example.employeemanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by username
    Optional<User> findByUsername(String username);

    // Find the role name associated with the given username
    @Query("SELECT r.name FROM Role r JOIN r.users u WHERE u.username = :username")
    String findRoleByUsername(@Param("username") String username);
}
