package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.NotificationsAndIssuesDTO;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.service.NotificationService;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/notificationanduser")
    public NotificationsAndIssuesDTO getNotificationsAndAssignedIssues(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return notificationService.getNotificationsAndIssuesForUser(user.getId());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@RequestHeader("Authorization") String jwt, @PathVariable("id") long notificationId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Marked as read");
    }
}
