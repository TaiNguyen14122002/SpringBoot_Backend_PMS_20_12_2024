package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Notification;
import com.TaiNguyen.ProjectManagementSystems.Modal.NotificationsAndIssuesDTO;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface NotificationService {


    public Notification createNotification(String content, Project project, Issue issue);

    public Notification markAsRead(long notificationId);

    public List<Notification> getNotificationsForUser(long userId);

    public Notification deleteNotification(long project);

    public NotificationsAndIssuesDTO getNotificationsAndIssuesForUser(long userId);
}
