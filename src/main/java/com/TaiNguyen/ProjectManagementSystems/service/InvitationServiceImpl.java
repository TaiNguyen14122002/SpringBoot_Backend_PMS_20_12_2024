package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Invitation;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.InvitationRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvitationServiceImpl implements InvitationService{

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailUtill emailUtill;


    @Override
    public void sendInvitation(String email, Long projectId, String name) throws MessagingException {

        String invitationToken = UUID.randomUUID().toString();

        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setProjectId(projectId);
        invitation.setToken(invitationToken);

        invitationRepository.save(invitation);

        String resetLink = "http://localhost:5173/accept_invitation?token=" + invitationToken;

        String subject = "bạn được mời tham gia vào dự án " + name;

        // Nội dung email với HTML và nút bấm
        String emailContent = "<h3>Tham gia dự án</h3>"
                + "<p>Bạn được mời tham gia vào dự án: " + name + "</p>"
                + "<p>Nhấn vào nút bên dưới nếu bạn muốn tham gia dự án:</p>"
                + "<a href=\"" + resetLink + "\" style=\"display:inline-block;background-color:#4CAF50;color:white;padding:10px 20px;text-align:center;text-decoration:none;font-size:16px;\">Tham gia dự án</a>"
                + "<p>Nếu bạn không muốn tham gia, hãy bỏ qua email này.</p>";


//        String invitationLink = "http://localhost:5173/accept_invitation?token=" + invitationToken;
//        emailService.sendEmailWithToken(email, invitationLink);

        emailUtill.sendEmail(email, subject, emailContent);
    }

    @Override
    public Invitation acceptInvitation(String token, Long userId) throws Exception {
        Invitation invitation = invitationRepository.findByToken(token);
        if(invitation == null){
            throw new Exception("Invalid invitation Token");
        }
        return invitation;
    }

    @Override
    public String getTokenByUserMail(String userEmail) {

        Invitation invitation = invitationRepository.findByEmail(userEmail);

        return invitation.getToken();
    }

    @Override
    public void deleteToken(String token) {

        Invitation invitation = invitationRepository.findByToken(token);

        invitationRepository.delete(invitation);

    }
}
