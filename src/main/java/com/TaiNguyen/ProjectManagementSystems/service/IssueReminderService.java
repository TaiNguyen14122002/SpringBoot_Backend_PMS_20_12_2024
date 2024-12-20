package com.TaiNguyen.ProjectManagementSystems.service;

import jakarta.mail.MessagingException;

public interface IssueReminderService {
    default void checkAndSendReminder() throws MessagingException {};

    public void sendReminderAt8AM() throws MessagingException;

    public void sendReminderAt3PM() throws MessagingException;

    public void sendReminderAt7PM() throws MessagingException;
}
