package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Notification;
import com.TaiNguyen.ProjectManagementSystems.Modal.NotificationsAndIssuesDTO;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.NotificationRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpI implements NotificationService{

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;


    @Override
    public Notification createNotification(String content, Project project, Issue issue) {
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setProject(project);
        notification.setIssue(issue);
        return notificationRepository.save(notification);
    }

    @Override
    public Notification markAsRead(long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(long userId) {
        return List.of();
    }

    @Override
    public Notification deleteNotification(long project) {
        notificationRepository.deleteByProjectId(project);
        return null;
    }

    @Override
    public NotificationsAndIssuesDTO getNotificationsAndIssuesForUser(long userId) {
        // Lấy danh sách các Project của người dùng
        List<Project> projects = projectRepository.findByOwnerId(userId);

        // Chuyển đổi danh sách Project thành danh sách ID của Project (Long)
        List<Long> projectIds = projects.stream()
                .map(Project::getId)  // Lấy ID của từng Project
                .collect(Collectors.toList());

        // Tìm các Notification bằng danh sách các Project ID
        List<Notification> notification = notificationRepository.findByProjectIdIn(projectIds);

        // Tìm các Issues đã được gán cho người dùng
        List<Issue> assignedIssues = issueRepository.findByAssigneeId(userId);

        // Trả về DTO chứa danh sách Notification và Issue
        return new NotificationsAndIssuesDTO(notification, assignedIssues);
    }

}
