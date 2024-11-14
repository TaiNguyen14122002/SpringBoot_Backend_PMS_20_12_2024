package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Notification;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;

import java.util.List;

public interface NotificationService {
    public Notification createNotification(String content, Project project, Issue issue);

    public Notification markAsRead(long notificationId);

    public List<Notification> getNotificationsForUser(long userId);
}
