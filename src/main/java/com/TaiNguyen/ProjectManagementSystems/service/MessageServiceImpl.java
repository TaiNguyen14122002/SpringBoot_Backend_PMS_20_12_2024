package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.MessageRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmailUtill emailUtill;


    @Override
    public Message sendMessage(Long senderId, Long projectId, String content) throws Exception {
        User sender = userRepository.findById(senderId).orElseThrow(()-> new Exception("User not found with id: " + senderId));

        Chat chat = projectService.getProjectById(projectId).getChat();

        Project project = projectService.getProjectById(projectId);

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());
        message.setChat(chat);
        Message savedMessage = messageRepository.save(message);
        chat.getMessages().add(savedMessage);

        sendEmailToTeam(project, sender);


        return savedMessage;
    }



    @Override
    public List<Message> getMessagesByProjectId(Long projectId) throws Exception {
        Chat chat = projectService.getChatByProjectId(projectId);
        List<Message> findByChatIdOrderByCreateAtAsc = messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
        return findByChatIdOrderByCreateAtAsc;
    }
//    @Override
//    public List<Message> getMessagesByProjectId(Long projectId) throws Exception {
//        Chat chat = projectService.getChatByProjectId(projectId);
//        List<Message> findByChatIdOrderByCreateAtAsc = messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
//        List<MessageDTO> messageDTOs = findByChatIdOrderByCreateAtAsc.stream()
//                .map(message -> new MessageDTO(message))
//                .collect(Collectors.toList());
//        return findByChatIdOrderByCreateAtAsc;
//    }

    @Override
    public void sendEmailToTeam(Project project, User sender) throws MessagingException {
        List<User> teamMembers = project.getTeam();
        String subject = "Thông báo tin nhắn mới trong dự án: " + project.getName();
        String loginLink = "https://react-js-frontend-pms-20-12-2024.vercel.app/project/" + project.getId();

        for (User user : teamMembers) {
            if (user.getEmail() != null) {
                String emailContent = "<!DOCTYPE html>"
                        + "<html lang=\"vi\">"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<title>Thông báo tin nhắn mới</title>"
                        + "</head>"
                        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4;\">"
                        + "<div style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                        + "<div style=\"background-color: #60A5FA; color: white; text-align: center; padding: 20px; border-radius: 8px 8px 0 0;\">"
                        + "<img src=\"https://firebasestorage.googleapis.com/v0/b/pms-fe88f.appspot.com/o/files%2FBlack%20and%20White%20Auto%20Repair%20Logo%20(1).png?alt=media&token=93954b37-4a53-43c2-a04e-7f49737cd55a\" alt=\"Logo công ty\" style=\"max-width: 150px; height: auto;\">"
                        + "<h1 style=\"margin: 10px 0;\">Thông báo tin nhắn mới</h1>"
                        + "</div>"
                        + "<div style=\"padding: 20px; text-align: center;\">"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Xin chào " + user.getFullname() + ",</p>"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Có một tin nhắn mới trong dự án của bạn:</p>"
                        + "<div style=\"background-color: #f0f0f0; border-left: 4px solid #60A5FA; padding: 15px; text-align: left; margin-bottom: 20px;\">"
                        + "<p style=\"font-size: 18px; font-weight: bold; margin-bottom: 10px;\">" + project.getName() + "</p>"
                        + "<p style=\"font-size: 16px; margin-bottom: 0;\">Người gửi: " + sender.getFullname() + "</p>"
                        + "</div>"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Vui lòng truy cập vào hệ thống để xem chi tiết và phản hồi.</p>"
                        + "</div>"
                        + "<div style=\"padding: 20px; text-align: center;\">"
                        + "<a href=\"" + loginLink + "\" style=\"display: inline-block; background-color: #60A5FA; color: white; padding: 12px 24px; text-align: center; text-decoration: none; font-size: 18px; border-radius: 4px; transition: background-color 0.3s;\">Xem tin nhắn</a>"
                        + "<p style=\"font-size: 14px; color: #666; margin-top: 20px;\">Nếu nút không hoạt động, hãy sao chép và dán liên kết sau vào trình duyệt của bạn:</p>"
                        + "<p style=\"font-size: 14px; color: #60A5FA;\">" + loginLink + "</p>"
                        + "</div>"
                        + "<div style=\"background-color: #f8f8f8; text-align: center; padding: 10px; font-size: 12px; color: #666; border-radius: 0 0 8px 8px;\">"
                        + "<p>© 2023 Project Management Systems. Mọi quyền được bảo lưu.</p>"
                        + "<p>Nếu bạn cần hỗ trợ, vui lòng liên hệ <a href=\"mailto:2024801030129@student.tdmu.edu.vn\" style=\"color: #60A5FA;\">2024801030129@student.tdmu.edu.vn</a></p>"
                        + "</div>"
                        + "</div>"
                        + "</body>"
                        + "</html>";

                emailUtill.sendEmail(user.getEmail(), subject, emailContent);
            }
        }
    }
}
