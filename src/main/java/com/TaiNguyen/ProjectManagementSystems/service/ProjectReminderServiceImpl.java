package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProjectReminderServiceImpl implements ProjectReminderService{

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailUtill emailUtill;

    @Override
    public void checkAndSendReminder() throws MessagingException {
        List<Project> projects = projectRepository.findByEndDateIsNotNull();

        for(Project project : projects) {
            if(!"done".equals(project.getStatus()) && isProjectExpiringSoon(project)){
                sendReminderEmail(project);
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?")  // 8h sáng
    public void sendReminderAt8AM() throws MessagingException {
        checkAndSendReminder();
    }

    @Override
    @Scheduled(cron = "0 0 15 * * ?")  // 3h chiều
    public void sendReminderAt3PM() throws MessagingException {
        checkAndSendReminder();
    }

    @Override
    @Scheduled(cron = "0 0 19 * * ?")  // 7h tối
    public void sendReminderAt7PM() throws MessagingException {
        checkAndSendReminder();
    }

    private boolean isProjectExpiringSoon(Project project) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalDate projectEndDate = project.getEndDate();
        long daysRemaining = currentDate.until(projectEndDate, ChronoUnit.DAYS);
        return daysRemaining <= 7;
    }

    private void sendReminderEmail(Project project) throws MessagingException {
        User user = project.getOwner();
        if(user != null){
            String subject = "Nhắc nhở: Dự án của bạn sắp hết hạn!";
            String message = createHtmlContent(user, project);
            emailUtill.sendEmail(user.getEmail(), subject, message);

        }
    }

    private String createHtmlContent(User user, Project project) {
        return "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Nhắc nhở về thời hạn dự án</title>"
                + "</head>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<div style='background-color: #f8f8f8; border: 1px solid #e1e1e1; border-radius: 5px; padding: 20px;'>"
                + "<h1 style='color: #2c3e50; text-align: center;'>Thông báo về thời hạn dự án</h1>"
                + "<p style='font-size: 16px;'>Kính gửi " + user.getFullname() + ",</p>"
                + "<p style='font-size: 16px;'>Dự án <strong style='color: #e74c3c;'>" + project.getName() + "</strong> của bạn sắp hết hạn vào ngày <strong>" + project.getEndDate() + "</strong>.</p>"
                + "<p style='font-size: 16px;'>Vui lòng thực hiện các hành động cần thiết để đảm bảo hoàn thành đúng thời hạn hoặc yêu cầu gia hạn nếu cần.</p>"
                + "<div style='background-color: #3498db; color: white; text-align: center; padding: 10px; border-radius: 5px;'>"
                + "<a href='http://localhost:5173/project/" + project.getId() + "' style='color: white; text-decoration: none; font-weight: bold;'>Xem chi tiết dự án</a>"
                + "</div>"
                + "<p style='font-size: 14px; margin-top: 20px; text-align: center;'>Nếu bạn có bất kỳ câu hỏi nào, xin đừng ngần ngại liên hệ với đội ngũ hỗ trợ của chúng tôi.</p>"
                + "</div>"
                + "<p style='font-size: 12px; color: #7f8c8d; text-align: center; margin-top: 20px;'>Đây là tin nhắn tự động. Vui lòng không trả lời email này.</p>"
                + "</body>"
                + "</html>";
    }
}
