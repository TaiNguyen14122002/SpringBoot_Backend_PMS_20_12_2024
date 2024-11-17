package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserIssueSalaryRepository extends JpaRepository<UserIssueSalary, Long> {
    List<UserIssueSalary> findByIssue(Issue issue);

    List<UserIssueSalary> findByUser(User user);
}
