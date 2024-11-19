package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.service.ProjectReminderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ProjectReminderService projectReminderService;

    @GetMapping("/sendReminder")
    public String sendReminderTest() throws MessagingException {
        projectReminderService.checkAndSendReminder();
        return "OK";
    }
}
