package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsAndIssuesDTO {

    private List<Notification> notifications;
    private List<Issue> assignedIssues;

}
