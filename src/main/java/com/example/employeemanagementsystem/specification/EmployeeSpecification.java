package com.example.employeemanagementsystem.specification;

import com.example.employeemanagementsystem.dto.EmployeeSearchRequestDTO;
import com.example.employeemanagementsystem.entity.Department;
import com.example.employeemanagementsystem.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    // For Admin: Search across all employees
    public static Specification<Employee> filterByCriteria(EmployeeSearchRequestDTO searchRequest) {
        return (root, query, criteriaBuilder) -> {
            Specification<Employee> spec = Specification.where(null);

            if (searchRequest.getName() != null && !searchRequest.getName().isEmpty()) {
                spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("firstName")), "%" + searchRequest.getName().toLowerCase() + "%"));
            }
            if (searchRequest.getDepartmentId() != null) {
                spec = spec.and((r, q, cb) -> 
                    cb.equal(r.get("department").get("id"), searchRequest.getDepartmentId())
                );
            }

            if (searchRequest.getJobTitle() != null && !searchRequest.getJobTitle().isEmpty()) {
                spec = spec.and((r, q, cb) -> cb.equal(r.get("jobTitle"), searchRequest.getJobTitle()));
            }
            if (searchRequest.getGender() != null && !searchRequest.getGender().isEmpty()) {
                spec = spec.and((r, q, cb) -> cb.equal(r.get("gender"), searchRequest.getGender()));
            }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }

    // For Manager: Search only within their department
    public static Specification<Employee> filterByCriteriaAndDepartment(EmployeeSearchRequestDTO searchRequest, Department department) {
        return (root, query, criteriaBuilder) -> {
            Specification<Employee> spec = filterByCriteria(searchRequest);

            // Restrict search to only manager's department
            spec = spec.and((r, q, cb) -> cb.equal(r.get("department"), department));

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}
