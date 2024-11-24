package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserIssueSalaryRepository extends JpaRepository<UserIssueSalary, Long> {
    List<UserIssueSalary> findByIssue(Issue issue);

    List<UserIssueSalary> findByUser(User user);

    Optional<UserIssueSalary> findByUserAndIssue(User user, Issue issue);

    List<UserIssueSalary> findAllByIssue_Project_Id(Long projectId);

    List<UserIssueSalary> findByUserAndIssueProject(User user, Project project);

    @Query("SELECT SUM(uis.salary) FROM UserIssueSalary uis WHERE uis.issue.project.id = ?1")
    BigDecimal findTotalSalaryByProjectId(@Param("projectId") long projectId);
}
