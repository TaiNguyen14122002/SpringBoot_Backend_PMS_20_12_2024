package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class IssueReminderServiceImpl implements IssueReminderService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailUtill emailUtill;

    @Override
    public void checkAndSendReminder() throws MessagingException {
        List<Issue> issues = issueRepository.findAll();
        for(Issue issue : issues) {
            if (isIssueExpiringSoon(issue)){
                sendReminderEmail(issue);
            }
        }
    }
    // Kiểm tra trạng thái của nhiệm vụ (Chưa hoàn thành)
    private boolean isIssueIncomplete(Issue issue) {
        // Giả sử trạng thái "completed" là nhiệm vụ đã hoàn thành, bạn có thể thay đổi theo trạng thái thực tế của bạn
        return !issue.getStatus().equalsIgnoreCase("Hoàn thành");
    }

    // Lập lịch gửi email vào lúc 8h sáng mỗi ngày
    @Override
    @Scheduled(cron = "0 0 8 * * ?")  // 8h sáng
    public void sendReminderAt8AM() throws MessagingException {
        checkAndSendReminder();
    }

    // Lập lịch gửi email vào lúc 3h chiều mỗi ngày
    @Override
    @Scheduled(cron = "0 0 15 * * ?")  // 3h chiều
    public void sendReminderAt3PM() throws MessagingException {
        checkAndSendReminder();
    }

    // Lập lịch gửi email vào lúc 7h tối mỗi ngày
    @Override
    @Scheduled(cron = "0 0 19 * * ?")  // 7h tối
    public void sendReminderAt7PM() throws MessagingException {
        checkAndSendReminder();
    }

    private boolean isIssueExpiringSoon(Issue issue) {
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalDate issueDueDate = issue.getDueDate();
        long daysRemaining = currentDate.until(issueDueDate, ChronoUnit.DAYS);
        return daysRemaining <= 7;  // Nếu nhiệm vụ còn 7 ngày hoặc ít hơn là sắp hết hạn
    }

    // Gửi email nhắc nhở cho thành viên được phân công và chủ dự án
    private void sendReminderEmail(Issue issue) throws MessagingException {

        if(!isIssueIncomplete(issue)){
            return;
        }
        User assignee = issue.getAssignee();
        Project project = issue.getProject();

        if (assignee != null) {
            String subject = "Nhắc nhở: Nhiệm vụ của bạn sắp hết hạn!";
            String message = createHtmlContentForAssignee(assignee, issue, project);
            emailUtill.sendEmail(assignee.getEmail(), subject, message);
        }

        if (project != null && project.getOwner() != null) {
            User projectOwner = project.getOwner();
            String subject = "Nhắc nhở: Nhiệm vụ trong dự án của bạn sắp hết hạn!";
            String message = createHtmlContentForProjectOwner(projectOwner, issue, project, assignee);
            emailUtill.sendEmail(projectOwner.getEmail(), subject, message);
        }
    }

    // Tạo nội dung HTML cho email gửi đến assignee
    private String createHtmlContentForAssignee(User assignee, Issue issue, Project project) {
        String remainingTime = calculateRemainingTime(issue.getDueDate());
        return "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Nhắc nhở về nhiệm vụ</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }"
                + "h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }"
                + "h2 { color: #34495e; }"
                + "strong { color: #e74c3c; }"
                + "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }"
                + "th, td { border: 1px solid #bdc3c7; padding: 10px; text-align: left; }"
                + "th { background-color: #ecf0f1; }"
                + "a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #3498db; color: #ffffff; text-decoration: none; border-radius: 5px; }"
                + "a:hover { background-color: #2980b9; }"
                + ".footer { margin-top: 20px; font-size: 0.8em; color: #7f8c8d; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<h1>Nhắc nhở về nhiệm vụ của bạn</h1>"
                + "<p>Chào " + assignee.getFullname() + ",</p>"
                + "<p>Đây là lời nhắc nhở về nhiệm vụ sắp đến hạn của bạn trong dự án <strong>" + project.getName() + "</strong>.</p>"
                + "<h2>Chi tiết nhiệm vụ:</h2>"
                + "<table>"
                + "<tr><th>Tiêu đề</th><td>" + issue.getTitle() + "</td></tr>"
                + "<tr><th>Mô tả</th><td>" + issue.getDescription() + "</td></tr>"
                + "<tr><th>Ngày bắt đầu</th><td>" + issue.getStartDate() + "</td></tr>"
                + "<tr><th>Ngày hết hạn</th><td><strong>" + issue.getDueDate() + "</strong></td></tr>"
                + "<tr><th>Thời gian còn lại</th><td><strong>" + remainingTime + "</strong></td></tr>"
                + "<tr><th>Trạng thái</th><td>" + issue.getStatus() + "</td></tr>"
                + "<tr><th>Độ ưu tiên</th><td>" + getPriorityInVietnamese(issue.getPriority()) + "</td></tr>"
                + "</table>"
                + "<p>Vui lòng hoàn thành nhiệm vụ trước thời hạn hoặc yêu cầu gia hạn nếu cần.</p>"
                + "<a href='http://localhost:5173/issue/" + issue.getId() + "'>Xem chi tiết nhiệm vụ</a>"
                + "<div class='footer'>"
                + "<p>Email này được gửi tự động từ hệ thống quản lý dự án. Vui lòng không trả lời email này.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    // Tạo nội dung HTML cho email gửi đến chủ dự án
    private String createHtmlContentForProjectOwner(User projectOwner, Issue issue, Project project, User assignee) {
        String remainingTime = calculateRemainingTime(issue.getDueDate());
        return "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Nhắc nhở về nhiệm vụ trong dự án của bạn</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }"
                + "h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }"
                + "h2 { color: #34495e; }"
                + "strong { color: #e74c3c; }"
                + "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }"
                + "th, td { border: 1px solid #bdc3c7; padding: 10px; text-align: left; }"
                + "th { background-color: #ecf0f1; }"
                + "a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #3498db; color: #ffffff; text-decoration: none; border-radius: 5px; }"
                + "a:hover { background-color: #2980b9; }"
                + ".footer { margin-top: 20px; font-size: 0.8em; color: #7f8c8d; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<h1>Nhắc nhở về nhiệm vụ trong dự án của bạn</h1>"
                + "<p>Chào " + projectOwner.getFullname() + ",</p>"
                + "<p>Đây là lời nhắc nhở về một nhiệm vụ sắp đến hạn trong dự án <strong>" + project.getName() + "</strong> của bạn.</p>"
                + "<h2>Chi tiết nhiệm vụ:</h2>"
                + "<table>"
                + "<tr><th>Tiêu đề</th><td>" + issue.getTitle() + "</td></tr>"
                + "<tr><th>Mô tả</th><td>" + issue.getDescription() + "</td></tr>"
                + "<tr><th>Người được giao</th><td>" + assignee.getFullname() + "</td></tr>"
                + "<tr><th>Ngày bắt đầu</th><td>" + issue.getStartDate() + "</td></tr>"
                + "<tr><th>Ngày hết hạn</th><td><strong>" + issue.getDueDate() + "</strong></td></tr>"
                + "<tr><th>Thời gian còn lại</th><td><strong>" + remainingTime + "</strong></td></tr>"
                + "<tr><th>Trạng thái</th><td>" + issue.getStatus() + "</td></tr>"
                + "<tr><th>Độ ưu tiên</th><td>" + getPriorityInVietnamese(issue.getPriority()) + "</td></tr>"
                + "</table>"
                + "<p>Vui lòng theo dõi và đảm bảo rằng nhiệm vụ này được hoàn thành đúng hạn.</p>"
                + "<a href='http://localhost:5173/project/" + project.getId() + "'>Xem chi tiết dự án</a>"
                + "<div class='footer'>"
                + "<p>Email này được gửi tự động từ hệ thống quản lý dự án. Vui lòng không trả lời email này.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private String getPriorityInVietnamese(String priority) {
        switch (priority.toLowerCase()) {
            case "low":
                return "Thấp";
            case "medium":
                return "Bình thường";
            case "high":
                return "Cao";
            default:
                return priority;
        }
    }

    private String calculateRemainingTime(LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(today, dueDate);

        if (daysRemaining < 0) {
            return "Đã quá hạn";
        } else if (daysRemaining == 0) {
            return "Hết hạn hôm nay";
        } else {
            return daysRemaining + " ngày";
        }
    }
}
