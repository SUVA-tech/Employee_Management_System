package com.example.employeemanagementsystem.config;

import com.example.employeemanagementsystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authorization.AuthorizationDecision;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Bean for password encoder using BCrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication provider setup using custom user details service and password encoder
    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Allow employees and managers to view their own profile
                .requestMatchers(HttpMethod.GET, "/employees/profile").hasAnyRole("EMPLOYEE", "MANAGER")

                // Custom access logic for fetching employee by ID
                .requestMatchers(HttpMethod.GET, "/employees/{id}").access((authentication, context) -> {
                    String username = authentication.get().getName();
                    Long requestedId = Long.parseLong(context.getVariables().get("id"));

                    // Allow access if user is ADMIN or MANAGER
                    if (authentication.get().getAuthorities().stream().anyMatch(roleAuth ->
                        roleAuth.getAuthority().equals("ROLE_ADMIN") || roleAuth.getAuthority().equals("ROLE_MANAGER"))) {
                        return new AuthorizationDecision(true);
                    }

                    // Allow EMPLOYEE to access their own details
                    boolean isAuthorized = userDetailsService.isAuthorizedEmployee(username, requestedId);
                    return new AuthorizationDecision(isAuthorized);
                })

                // ADMIN: Full access to manage employees and users
                .requestMatchers(HttpMethod.POST, "/employees/addEmployee").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/employees/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/employees/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/auth/signup").hasRole("ADMIN")

                // MANAGER and ADMIN: Can view employee lists and generate reports
                .requestMatchers(HttpMethod.GET, "/employees").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/employees/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/reports/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/employees/search").hasAnyRole("ADMIN", "MANAGER")

                // Allow login and signup endpoint access to everyone
                .requestMatchers(HttpMethod.GET, "/auth/login", "/auth/signup").permitAll()

                // Require authentication for any other request
                .anyRequest().authenticated()
            )
            // Enable HTTP Basic authentication
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
