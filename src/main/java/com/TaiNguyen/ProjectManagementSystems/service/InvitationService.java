package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Invitation;
import jakarta.mail.MessagingException;

public interface InvitationService {

    public void sendInvitation (String email, Long projectId, String name) throws MessagingException;
    public Invitation acceptInvitation(String token, Long userId) throws Exception;

    public String getTokenByUserMail(String userEmail);

    void deleteToken(String token);
}
