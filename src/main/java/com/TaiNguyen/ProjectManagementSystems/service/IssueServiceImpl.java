package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserIssueSalaryRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueServiceImpl implements IssueService{

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserIssueSalaryRepository userIssueSalaryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailUtill emailUtill;


    @Override
    public Issue getIssueById(Long issueId) throws Exception {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if(issue.isPresent()){
            return issue.get();
        }
        throw new Exception("No issues found with issueid" + issueId);
    }

    @Override
    public List<Issue> getIssuesByUserId(User user) throws Exception {
        return issueRepository.findByAssignee(user);
    }


    @Override
    public List<Issue> getIssueByProjectId(Long projectId) throws Exception {
        return issueRepository.findByProjectId(projectId);
    }

    @Override
    public Issue createIssue(IssueRequest issueRequest, User user) throws Exception {
        Project project = projectService.getProjectById(issueRequest.getProjectId());

        Issue issue = new Issue();
        issue.setTitle(issueRequest.getTitle());
        issue.setDescription(issueRequest.getDescription());
        issue.setStatus("Chưa làm");
        issue.setProjectID(issueRequest.getProjectId());
        issue.setPriority(issueRequest.getPriority());
        issue.setDueDate(issueRequest.getDueDate());
        issue.setStartDate(issueRequest.getStartDate());
        issue.setPrice(issueRequest.getPrice());

        issue.setProject(project);

        return issueRepository.save(issue);
    }

    @Override
    public void deleteIssue(Long issueId, Long userid) throws Exception {
        getIssueById(issueId);
        issueRepository.deleteById(issueId);
    }

    @Override
    public Issue addUserToIssue(Long issueId, Long userId) throws Exception {
        User user = userService.findUserById(userId);
        Issue issue = getIssueById(issueId);

         issue.setAssignee(user);
         return issueRepository.save(issue);

    }

    @Override
    public Issue updateIssue(Long issueId, String status) throws Exception {
        Issue issue = getIssueById(issueId);

        issue.setStatus(status);
        issue.setActualDate(LocalDate.now());

        Issue updatedIssue = issueRepository.save(issue);
        sendNotificationEmail(issue);
        return updatedIssue;
//        return issueRepository.save(issue);

    }

    private void sendNotificationEmail(Issue issue) throws Exception {
        // Lấy thông tin Owner của Issue
        User owner = issue.getProject().getOwner(); // Đảm bảo Issue có phương thức getOwner()

        if (owner != null && owner.getEmail() != null) {
            String subject = "Thông báo thay đổi trạng thái Issue";
            String issueUrl = "https://react-js-frontend-pms-20-12-2024.vercel.app/project/" + issue.getProject().getId() + "issue/" + issue.getId(); // Giả định URL

            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html lang='vi'>" +
                            "<head>" +
                            "    <meta charset='UTF-8'>" +
                            "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                            "    <title>" + subject + "</title>" +
                            "</head>" +
                            "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                            "    <div style='text-align: center; margin-bottom: 20px;'>" +
                            "        <img src='https://your-company-logo.com/logo.png' alt='Logo Công ty' style='max-width: 150px;'>" +
                            "    </div>" +
                            "    <h1 style='color: #4a4a4a; text-align: center;'>" + subject + "</h1>" +
                            "    <p>Chào <strong>" + owner.getFullname() + "</strong>,</p>" +
                            "    <p>Trạng thái của Issue sau đã được cập nhật:</p>" +
                            "    <div style='background-color: #f9f9f9; border-left: 4px solid #5c6bc0; padding: 15px; margin: 20px 0;'>" +
                            "        <p><strong>Tiêu đề:</strong> " + issue.getTitle() + "</p>" +
                            "        <p><strong>ID:</strong> " + issue.getId() + "</p>" +
                            "        <p><strong>Trạng thái mới:</strong> <span style='color: #4caf50; font-weight: bold;'>" + issue.getStatus() + "</span></p>" +
                            "        <p><strong>Ngày thực tế:</strong> " + issue.getActualDate() + "</p>" +
                            "    </div>" +
                            "    <div style='text-align: center; margin-top: 30px;'>" +
                            "        <a href='" + issueUrl + "' style='background-color: #5c6bc0; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Xem chi tiết Issue</a>" +
                            "    </div>" +
                            "    <p style='margin-top: 30px; text-align: center; color: #888;'>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>" +
                            "</body>" +
                            "</html>";

            // Sử dụng EmailUtil hoặc EmailService để gửi email
            emailUtill.sendEmail(owner.getEmail(), subject, htmlContent);
        } else {
            throw new Exception("Không thể gửi email. Chủ dự án hoặc email không tồn tại.");
        }
    }

    @Override
    public Issue updateFinishIssue(Long issueId, String finish) throws Exception {
        Issue issue = getIssueById(issueId);
        issue.setFinish(finish);
        return issueRepository.save(issue);
    }

    @Override
    public boolean checkProjectOwner(Long projectId, Long userId) throws Exception {
        Project project = projectService.getProjectById(projectId);

        if (project != null && project.getOwner() != null && project.getOwner().getId() == userId) {
            return true;
        }

        return false;

    }

    @Override
    public Map<String, Long> getIssueCountByStatus(Long projectId) throws Exception {
        List<Issue> issues = issueRepository.findByProjectId(projectId);

        long inProgressCount = issues.stream()
                .filter(issue -> "Chưa làm".equalsIgnoreCase(issue.getStatus()))
                .count();
        long notStartedCount = issues.stream()
                .filter(issue -> "Đang làm".equalsIgnoreCase(issue.getStatus()))
                .count();
        long doneCount = issues.stream()
                .filter(issue -> "Hoàn thành".equalsIgnoreCase(issue.getStatus()))
                .count();

        Map<String, Long> issueCountByStatus = new HashMap<>();
        issueCountByStatus.put("inprogress", inProgressCount);
        issueCountByStatus.put("notstarted", notStartedCount);
        issueCountByStatus.put("done", doneCount);

        return issueCountByStatus;
    }

    @Override
    public List<Issue> allIssuesByAssigneeId(Long userId) throws Exception {
        return issueRepository.findAllIssueByAssigneeId(userId);

    }

    @Override
    public List<Issue> getIssueByProjectAndAssigneeId(Long projectId, Long userId) throws Exception {
        return issueRepository.findByProject_IdAndAssignee_Id(projectId, userId);
    }

    @Override
    public Map<String, Long> getUserIssueStatisticsByProject(Long userId, Long projectId) {
        Map<String, Long> issueStatistics = new HashMap<>();
        issueStatistics.put("total", issueRepository.countTotalIssuesByUserAndProject(userId, projectId));
        issueStatistics.put("pending", issueRepository.countTodoIssuesByUserAndProject(userId, projectId));
        issueStatistics.put("inProgress", issueRepository.countInProgressIssuesByUserAndProject(userId, projectId));
        issueStatistics.put("done", issueRepository.countDoneIssuesByUserAndProject(userId, projectId));
        return issueStatistics;

    }

    public List<Map<String, Object>> getIssueCountByPriorityForUserProjects(Long userId) {
        List<Object[]> results = issueRepository.countIssuesByPriorityForUserProjects(userId);
        List<Map<String, Object>> priorityCounts = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("priority", result[0]);
            data.put("count", result[1]);
            priorityCounts.add(data);
        }
        return priorityCounts;
    }

    @Override
    public Map<String, Long> getIssueCountByPriority(Long projectId) {
        List<Object[]> results = issueRepository.countIssuesByPriority(projectId);
        Map<String, Long> priorityCounts = new HashMap<>();
        for (Object[] result : results) {
            String priority = (String) result[0];
            Long count = (Long) result[1];
            priorityCounts.put(priority, count);
        }
        return priorityCounts;
    }

    @Override
    public Map<String, Long> getIssueCountByStatuss(Long projectId) {
        List<Object[]> results = issueRepository.countIssuesByStatus(projectId);
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status, count);
        }
        return statusCounts;
    }

    @Override
    public Map<String, Map<String, Long>> getIssueCountByStatusAndAssignee(Long projectId) {
        List<Object[]> results = issueRepository.countIssuesByStatusAndAssignee(projectId);
        Map<String, Map<String, Long>> statusCounts = new HashMap<>();

        for(Object[] result : results) {
            String assigneeName = result[0] != null ? (String) result[0] : "Chưa phân công";
            String status = (String) result[1];
            Long count = (Long) result[2];

            statusCounts.computeIfAbsent(assigneeName, k -> new HashMap<>())
                    .put(status, count);
        }
        return statusCounts;
    }

    @Override
    public double getIssueDoneRatioForProject(Long projectId) {
        long completedCount = issueRepository.countCompletedIssuesByProject(projectId);
        long inProgressOrNotStartedCount = issueRepository.countInProgressOrNotStartedIssuesByProject(projectId);

        long totalCount = completedCount + inProgressOrNotStartedCount;
        return ((int) completedCount * 100 / totalCount);
    }

    @Override
    public void uploadFileToIssue(Long IssueId, Issue file) throws Exception {
        Issue issue = getIssueById(IssueId);
        issue.getFileNames().addAll(file.getFileNames());
        issueRepository.save(issue);
    }

    @Override
    public List<Issue> getAllIssuesByOwnerId(Long ownerId) throws Exception {
        return issueRepository.findAllIssuesByOwnerId(ownerId);
    }

    @Override
    public List<IssueDTO> getIssuesByProject(long projectId) {
        // Lấy Project theo projectId
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Lấy tất cả các Issues của Project
        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        // Chuyển đổi các Issue thành IssueDTO
        return issues.stream().map(issue -> {
            IssueDTO issueDTO = new IssueDTO();
            issueDTO.setId(issue.getId());
            issueDTO.setTitle(issue.getTitle());
            issueDTO.setDescription(issue.getDescription());
            issueDTO.setStatus(issue.getStatus());
            issueDTO.setProjectId(issue.getProjectID());
            issueDTO.setPriority(issue.getPriority());
            issueDTO.setStartDate(issue.getStartDate());
            issueDTO.setAssignes(issue.getAssignee());
            issueDTO.setDueDate(issue.getDueDate());
            issueDTO.setPrice(issue.getPrice());
            issueDTO.setFinish(issue.getFinish());
            issueDTO.setTags(issue.getTags());

            // Lấy danh sách UserIssueSalary cho mỗi Issue
            List<UserIssueSalary> salaries = userIssueSalaryRepository.findByIssue(issue);

            // Chuyển đổi danh sách UserIssueSalary thành UserIssueSalaryDTO
            List<UserIssueSalaryDTO> userIssueSalaryDTOs = salaries.stream()
                    .map(salary -> {
                        UserIssueSalaryDTO salaryDTO = new UserIssueSalaryDTO();
                        salaryDTO.setId(salary.getId());
                        salaryDTO.setUser(salary.getUser());  // Giả sử User có fullName
                        salaryDTO.setSalary(salary.getSalary());
                        salaryDTO.setCurrency(salary.getCurrency());
                        salaryDTO.setPaid(salary.isPaid());
                        return salaryDTO;
                    })
                    .collect(Collectors.toList());

            issueDTO.setUserIssueSalaries(userIssueSalaryDTOs);

            return issueDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> getIssuesByUser(long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User ID is null");



        List<UserIssueSalary> userIssueSalaries= userIssueSalaryRepository.findByUser(user);

        List<Issue> issues = issueRepository.findByAssignee(user);

//        List<Issue> issues = issueRepository.findByProjectId(project.getId());

//        List<Issue> issues = userIssueSalaries.stream()
//                .map(UserIssueSalary::getIssue)
//                .distinct()
//                .collect(Collectors.toList());
//
        return issues.stream().map(issue -> {
            IssueDTO issueDTO = new IssueDTO();
            issueDTO.setId(issue.getId());
            issueDTO.setTitle(issue.getTitle());
            issueDTO.setDescription(issue.getDescription());
            issueDTO.setStatus(issue.getStatus());
            issueDTO.setProjectId(issue.getProjectID());
            issueDTO.setPriority(issue.getPriority());
            issueDTO.setStartDate(issue.getStartDate());
            issueDTO.setAssignes(issue.getAssignee());
            issueDTO.setDueDate(issue.getDueDate());
            issueDTO.setPrice(issue.getPrice());
            issueDTO.setFinish(issue.getFinish());
            issueDTO.setTags(issue.getTags());

            List<UserIssueSalaryDTO> salaries = userIssueSalaries.stream()
                    .filter(salary -> salary.getIssue().getId() == issue.getId())
                    .map(salary -> {
                        UserIssueSalaryDTO salaryDTO = new UserIssueSalaryDTO();
                        salaryDTO.setId(salary.getId());
                        salaryDTO.setUser(salary.getUser());
                        salaryDTO.setSalary(salary.getSalary());
                        salaryDTO.setCurrency(salary.getCurrency());
                        salaryDTO.setPaid(salary.isPaid());
                        return salaryDTO;
                    }).collect(Collectors.toList());

            issueDTO.setUserIssueSalaries(salaries);
            return issueDTO;
        }).collect(Collectors.toList());


    }

    @Override
    public List<Object[]> getAllIssuesWithSalaryByUserId(long userId) throws Exception {
        return issueRepository.findAllIssuesWithSalaryByUserId(userId);
    }

    @Override
    public List<Issue> getExpiringIssues(User assignee) throws Exception {
        LocalDate currentDate = LocalDate.now();
        System.out.println("Current Date: " + currentDate);
        LocalDate endDate = currentDate.plusDays(7);
        return issueRepository.findByAssigneeAndDueDateBetweenAndStatusNot(
                assignee, currentDate, endDate, "Hoàn thành");
    }

//    @Override
//    public List<Issue> getExpiredIssues(User assignee) throws Exception {
//        LocalDate currentDate = LocalDate.now();
//        return issueRepository.findByAssigneeAndDueDateBeforeAndStatusNot(assignee, currentDate, "Hoàn thành");
//    }

    @Override
    public List<IssueResponseDTO> getExpiredIssues(User assignee) throws Exception {
        LocalDate currentDate = LocalDate.now();

        // Truy vấn nhiệm vụ người dùng làm chủ (owner) và người được phân công (assignee)
        List<Issue> issues = issueRepository.findByAssigneeOrProjectOwnerAndDueDateBeforeAndStatusNot(
                assignee, assignee, currentDate, "Hoàn thành");

        // Lọc các nhiệm vụ có trạng thái khác "Hoàn thành" nếu vẫn hiển thị
        issues = issues.stream()
                .filter(issue -> !"Hoàn thành".equals(issue.getStatus()))
                .collect(Collectors.toList());

        // Xử lý danh sách nhiệm vụ để chuyển sang DTO
        return issues.stream()
                .map(issue -> {
                    boolean isOwner = issue.getAssignee() != null && issue.getAssignee().equals(assignee);

                    // Kiểm tra assignee nếu null, tạo thông tin mặc định
                    AssigneeResponse assigneeResponse;
                    if (issue.getAssignee() == null) {
                        assigneeResponse = new AssigneeResponse(
                                null, // ID null
                                "Chưa phân công", // Fullname "Chưa phân công"
                                "", // Email rỗng
                                "", // Address rỗng
                                null, // CreatedDate null
                                "", // Phone rỗng
                                "", // Company rỗng
                                "", // Position rỗng
                                null, // Skills rỗng
                                "", // Introduce rỗng
                                "", // Avatar rỗng
                                0 // ProjectSize 0
                        );
                    } else {
                        assigneeResponse = new AssigneeResponse(
                                issue.getAssignee().getId(),
                                issue.getAssignee().getFullname(),
                                issue.getAssignee().getEmail(),
                                issue.getAssignee().getAddress(),
                                issue.getAssignee().getCreatedDate(),
                                issue.getAssignee().getPhone(),
                                issue.getAssignee().getCompany(),
                                issue.getAssignee().getProgramerposition(),
                                issue.getAssignee().getSelectedSkills(),
                                issue.getAssignee().getIntroduce(),
                                issue.getAssignee().getAvatar(),
                                issue.getAssignee().getProjectSize()
                        );
                    }

                    // Tạo đối tượng IssueResponseDTO từ dữ liệu nhiệm vụ và assignee
                    return new IssueResponseDTO(
                            issue.getId(),
                            issue.getTitle(),
                            issue.getDescription(),
                            issue.getStatus(),
                            issue.getProjectID(),
                            issue.getPriority(),
                            issue.getStartDate(),
                            issue.getDueDate(),
                            issue.getActualDate(),
                            issue.getPrice(),
                            issue.getFinish(),
                            issue.getTags(),
                            assigneeResponse, // Thêm assignee vào DTO
                            isOwner,
                            issue.getSalary(),
                            issue.getFileNames()
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    public Issue updateDueDate(User Owner, Long issueId, LocalDate dueDate) throws MessagingException {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new RuntimeException("Issue not found"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        String issueUrl = "https://react-js-frontend-pms-20-12-2024.vercel.app/project/" + issue.getProjectID() + "/issue/" + issue.getId();

        Project project = issue.getProject();
        if(!project.getOwner().equals(Owner)) {
            String subject = "Yêu cầu phê duyệt gia hạn nhiệm vụ: " + issue.getTitle();



//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            String formattedDueDate = dateFormat.format(dueDate);
//            String currentDueDate = dateFormat.format(issue.getDueDate());
            String htmlBody =
                    "<html>" +
                            "<head>" +
                            "<style>" +
                            "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                            ".container { width: 100%; max-width: 600px; margin: 0 auto; }" +
                            ".header { background-color: #f4f4f4; padding: 20px; text-align: center; }" +
                            ".content { padding: 20px; }" +
                            ".footer { background-color: #f4f4f4; padding: 10px; text-align: center; font-size: 12px; }" +
                            "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }" +
                            "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                            "th { background-color: #f2f2f2; }" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class='container'>" +
                            "<div class='header'>" +
                            "<h2>Yêu cầu gia hạn nhiệm vụ</h2>" +
                            "</div>" +
                            "<div class='content'>" +
                            "<p>Kính gửi " + project.getOwner().getFullname() + ",</p>" +
                            "<p>Tôi hy vọng email này tìm thấy bạn trong tình trạng tốt. " +
                            "Tôi viết thư này để yêu cầu gia hạn thời gian cho nhiệm vụ sau:</p>" +
                            "<table>" +
                            "<tr><th>Tiêu đề nhiệm vụ</th><td>" + issue.getTitle() + "</td></tr>" +
                            "<tr><th>Mã nhiệm vụ</th><td>" + " NV " + issue.getId() + "</td></tr>" +
                            "<tr><th>Ngày đến hạn hiện tại</th><td>" + issue.getDueDate() + "</td></tr>" +
                            "<tr><th>Ngày đến hạn đề xuất</th><td>" + dueDate + "</td></tr>" +
                            "</table>" +
                            "<p><strong>Lý do gia hạn:</strong> [Vui lòng điền lý do ở đây]</p>" +
                            "<p>Tôi kính mong bạn xem xét yêu cầu này và phê duyệt gia hạn đến ngày " +
                            dueDate + ". Nếu bạn cần thêm thông tin hoặc có bất kỳ câu hỏi nào, " +
                            "xin vui lòng liên hệ với tôi.</p>" +
                            "<p>Để xem chi tiết và phê duyệt yêu cầu, vui lòng nhấp vào nút bên dưới:</p>" +
                            "<p style='text-align: center;'>" +
                            "<a href='" + issueUrl + "' class='button'>Xem chi tiết nhiệm vụ</a>" +
                            "</p>" +
                            "<p>Cảm ơn bạn đã dành thời gian xem xét yêu cầu này.</p>" +
                            "<p>Trân trọng,<br>" + Owner.getFullname() + "</p>" +
                            "</div>" +
                            "<div class='footer'>" +
                            "Email này được gửi tự động từ hệ thống quản lý dự án. Vui lòng không trả lời email này." +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            emailUtill.sendEmail(project.getOwner().getEmail(), subject,htmlBody);
            throw new RuntimeException("Bạn không có quyền chỉnh sửa");


        }
        issue.setDueDate(dueDate);
        Issue updatedIssue = issueRepository.save(issue);

        String successSubject = "Nhiệm vụ đã được gia hạn: " + issue.getTitle();
        String successHtmlBody =
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        ".container { width: 100%; max-width: 600px; margin: 0 auto; }" +
                        ".header { background-color: #f4f4f4; padding: 20px; text-align: center; }" +
                        ".content { padding: 20px; }" +
                        ".footer { background-color: #f4f4f4; padding: 10px; text-align: center; font-size: 12px; }" +
                        "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }" +
                        "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                        "th { background-color: #f2f2f2; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "<div class='header'>" +
                        "<h2>Nhiệm vụ đã được gia hạn</h2>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<p>Kính gửi " + issue.getAssignee().getFullname() + ",</p>" +
                        "<p>Chúng tôi muốn thông báo rằng nhiệm vụ sau đã được gia hạn:</p>" +
                        "<table>" +
                        "<tr><th>Tiêu đề nhiệm vụ</th><td>" + issue.getTitle() + "</td></tr>" +
                        "<tr><th>Mã nhiệm vụ</th><td>" + " NV " + issue.getId() + "</td></tr>" +
                        "<tr><th>Ngày đến hạn mới</th><td>" + dueDate + "</td></tr>" +
                        "</table>" +
                        "<p>Vui lòng tiếp tục theo dõi và hoàn thành nhiệm vụ trước ngày đến hạn mới.</p>" +
                        "<p>Trân trọng,<br>Hệ thống quản lý dự án</p>" +
                        "<a href='" + issueUrl + "' class='button'>Xem chi tiết nhiệm vụ</a>" +  // Thêm nút dẫn đến nhiệm vụ
                        "</div>" +
                        "<div class='footer'>" +
                        "Email này được gửi tự động từ hệ thống quản lý dự án. Vui lòng không trả lời email này." +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";

        // Gửi email thông báo gia hạn thành công tới thành viên thực hiện nhiệm vụ
        emailUtill.sendEmail(issue.getAssignee().getEmail(), successSubject, successHtmlBody);
        return issueRepository.save(issue);


    }

    @Override
    public Optional<Issue> findByIdAndProject(long issueId, Project project) {
        return issueRepository.findByIdAndProject(issueId, project);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?")  // 9h sáng
    @Scheduled(cron = "0 0 14 * * ?") // 2h chiều
    @Scheduled(cron = "0 0 17 * * ?") // 5h chiều
    @Scheduled(cron = "0 0 19 * * ?") // 7h tối
    public void checkAndNotifyIssuesDueSoon() throws MessagingException {
        List<Issue> issuesDueSoon = getIssuesDueSoon();
        for(Issue issue : issuesDueSoon) {
            sendNotification(issue);
        }
    }

    @Override
    public List<Issue> getIssuesDueSoon() {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(2);
        return issueRepository.findByDueDateBefore(threshold);
    }

    private void sendNotification(Issue issue) throws MessagingException {
        User assignee = issue.getAssignee();
        Project project = issue.getProject();
        if(assignee != null){
            String subject = "Nhắc nhở: Nhiệm vụ " + issue.getTitle() + " của bạn sắp hết hạn!";
            String message = createHtmlContentForAssignee(assignee, issue, project);
            emailUtill.sendEmail(assignee.getEmail(), subject, message);
        }

        if(project != null){
            User projectOwner = project.getOwner();
            if(projectOwner != null){
                String subject = "Nhắc nhở: Nhiệm vụ" + issue.getTitle() + " trong dự án của bạn sắp hết hạn!";
                String message = createHtmlContentForProjectOwner(projectOwner, issue, project, assignee);
                emailUtill.sendEmail(projectOwner.getEmail(), subject, message);
            }
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



}
