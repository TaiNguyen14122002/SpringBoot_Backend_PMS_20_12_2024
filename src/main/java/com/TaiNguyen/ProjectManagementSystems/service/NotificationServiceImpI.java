package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Notification;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpI implements NotificationService{

    @Autowired
    private NotificationRepository notificationRepository;


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
}
