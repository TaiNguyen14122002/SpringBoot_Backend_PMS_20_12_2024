package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Notification;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByisRead(boolean Read);
    List<Notification> findByProject(Project project);
    List<Notification> findByIssue(Issue issue);
}
