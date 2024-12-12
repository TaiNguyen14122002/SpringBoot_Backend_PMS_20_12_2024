package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserIssueSalaryRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import com.TaiNguyen.ProjectManagementSystems.response.MessageResponse;
import com.TaiNguyen.ProjectManagementSystems.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueExportExcelService issueExportExcelService;

    @Autowired
    private UserIssueSalaryRepository userIssueSalaryRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{issueId}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long issueId) throws Exception {
        return ResponseEntity.ok(issueService.getIssueById(issueId));
    }

    @GetMapping("/Asignee")
    public ResponseEntity<List<Issue>> getIssueByAsignee(@RequestHeader("Authorization") String token) throws Exception {
        try{
            User tokenUser = userService.findUserProfileByJwt(token);
            User user = userService.findUserById(tokenUser.getId());

            List<Issue> issues = issueService.getIssuesByUserId(user);
            return ResponseEntity.ok(issues);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Issue>> getIssueByProjectId(@PathVariable Long projectId)
            throws Exception {
        return ResponseEntity.ok(issueService.getIssueByProjectId(projectId));
    }

    @PostMapping()
    public ResponseEntity<IssueDTO> createIssue(@RequestBody IssueRequest issue,
                                                @RequestHeader("Authorization") String token)
        throws Exception{
        User tokenUser = userService.findUserProfileByJwt(token);
        User user = userService.findUserById(tokenUser.getId());

        Issue createdIssue = issueService.createIssue(issue, tokenUser);
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setDescription(createdIssue.getDescription());
        issueDTO.setDueDate(createdIssue.getDueDate());
        issueDTO.setId(createdIssue.getId());
        issueDTO.setPriority(createdIssue.getPriority());
        issueDTO.setProject(createdIssue.getProject());
        issueDTO.setProjectId(createdIssue.getProjectID());
        issueDTO.setStatus(createdIssue.getStatus());
        issueDTO.setTitle(createdIssue.getTitle());
        issueDTO.setTags(createdIssue.getTags());
        issueDTO.setAssignes(createdIssue.getAssignee());
        issueDTO.setPrice(createdIssue.getPrice());
        issueDTO.setFinish(createdIssue.getFinish());

        String DateNow = LocalDate.now().toString();
        String notificationContent = "Bạn đã thêm nhiệm vụ \"" + createdIssue.getTitle() +" trong dự án " + createdIssue.getProject().getName() +   " vào lúc " + DateNow;
        notificationService.createNotification(notificationContent, createdIssue.getProject(), createdIssue);

        return ResponseEntity.ok(issueDTO);
    }

    @DeleteMapping("/{issueId}")
    public ResponseEntity<MessageResponse> deleteIssue(@PathVariable Long issueId,
                                                    @RequestHeader("Authorization") String token)
        throws Exception{
        User user = userService.findUserProfileByJwt(token);
        issueService.deleteIssue(issueId, user.getId());

        MessageResponse res = new MessageResponse();
        res.setMessage("Issue deleted");

        return ResponseEntity.ok(res);
    }

    @PutMapping("/{issueId}/assignee/{userId}")
    public ResponseEntity<Issue> addUserToIssue(@PathVariable Long issueId,
                                                @PathVariable Long userId)
        throws Exception{
        Issue issue = issueService.addUserToIssue(issueId, userId);

        return ResponseEntity.ok(issue);
    }
    @PutMapping("/{issueId}/status/{status}")
    public ResponseEntity<Issue> updateIssueStatus(
            @PathVariable String status,
            @PathVariable Long issueId) throws Exception{
        Issue issue = issueService.updateIssue(issueId, status);
        return ResponseEntity.ok(issue);
    }

    @PutMapping("/{issueId}/finish/{finish}")
    public ResponseEntity<Issue> updateFinishIssue(@PathVariable String finish, @PathVariable Long issueId) throws  Exception{
        Issue issue = issueService.updateFinishIssue(issueId, finish);

        return ResponseEntity.ok(issue);
    }



    @GetMapping("/project/{projectId}/status")
    public ResponseEntity<?> getIssuesCountByStatus(@RequestHeader("Authorization") String token,
                                                    @PathVariable Long projectId) {
        try {
            // Xác thực người dùng từ JWT token
            User user = userService.findUserProfileByJwt(token);

            // Kiểm tra quyền sở hữu dự án
            boolean isOwner = issueService.checkProjectOwner(projectId, user.getId());
            if (!isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền truy cập thông tin này");
            }

            // Lấy số lượng công việc theo trạng thái
            Map<String, Long> issueCounts = issueService.getIssueCountByStatus(projectId);
            return ResponseEntity.ok(issueCounts);

        } catch (Exception e) {
            // Xử lý ngoại lệ và trả về thông báo lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý");
        }
    }


    @GetMapping("/AllIssueByAssigneedId")
    public List<Issue> AllIssueByAssigneeId( @RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        return issueService.allIssuesByAssigneeId(user.getId());
    }

    @GetMapping("/GetIssueByProjectIdAndUserId")
    public List<Issue> getIssuesByProjectAndUser( @RequestHeader("Authorization") String jwt, @RequestParam Long projectId) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        return issueService.getIssueByProjectAndAssigneeId(projectId, user.getId());
    }

    @GetMapping("/statistics/project/{projectId}")
    public ResponseEntity<Map<String, Long>> getUserIssueStatisticsByProject(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long projectId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Map<String, Long> stats = issueService.getUserIssueStatisticsByProject(user.getId(), projectId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/priority-count/all-projects")
    public ResponseEntity<List<Map<String, Object>>> getIssueCountByPriorityForAllProjects(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Map<String, Object>> data = issueService.getIssueCountByPriorityForUserProjects(user.getId());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/countByPriority/{projectId}")
    public Map<String, Long> getIssueCountByPriority(@PathVariable Long projectId){
        return issueService.getIssueCountByPriority(projectId);
    }

    @GetMapping("/countByStatus/{projectId}")
    public Map<String, Long> getIssueCountsByStatus(@PathVariable Long projectId) {
        return issueService.getIssueCountByStatuss(projectId);
    }

    @GetMapping("/countByStatusAndAssignee/{projectId}")
    public Map<String, Map<String, Long>> getUserIssueStatisticsByAssigneeForAllProjects(@PathVariable Long projectId){
        return issueService.getIssueCountByStatusAndAssignee(projectId);
    }

    @GetMapping("/projects/completion-ratio")
    public double getCompletionRatioForProject(@RequestParam Long projectId) {
        return issueService.getIssueDoneRatioForProject(projectId);
    }

    @PutMapping("/uploadFileToIssue/{IssueId}/upload")
    public String uploadFile(@PathVariable Long IssueId, @RequestBody Issue files) throws Exception {
        try {
            issueService.uploadFileToIssue(IssueId, files);
            return "File uploaded successfully";
        }catch (Exception e){
            return "Error occured while uploading file" + e.getMessage();
        }
    }

    @GetMapping("/allissue")
    public ResponseEntity<List<Issue>> getAllIssue(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            if (user != null) {
                List<Issue> allIssues = issueRepository.findAll();
                return ResponseEntity.ok(allIssues);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Issue>> getAllIssuesByOwnerId(@RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        List<Issue> issues = issueService.getAllIssuesByOwnerId(user.getId());
        return ResponseEntity.ok(issues);
    }


    @GetMapping("/projects/{projectId}/issues")
    public List<IssueDTO> getIssuesByProject(@PathVariable long projectId) throws Exception {
        return issueService.getIssuesByProject(projectId);
    }

    @GetMapping("/users/issues")
    public List<IssueDTO> getIssueByUser(@RequestHeader("Authorization") String jwt) throws Exception {
            User user = userService.findUserProfileByJwt(jwt);
            return issueService.getIssuesByUser(user.getId());

    }

    @GetMapping("/expiring")
    public List<Issue> getExpiringIssues(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return issueService.getExpiringIssues(user);
    }

    @GetMapping("/expired")
    public List<Issue> getExpiredIssues (@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return issueService.getExpiredIssues(user);
    }

    @PutMapping({"/{issueId}/due-date"})
    public ResponseEntity<Issue> updateDueDate(@PathVariable Long issueId, @RequestBody LocalDate dueDate){
        Issue updatedIssue = issueService.updateDueDate(issueId, dueDate);
        return ResponseEntity.ok(updatedIssue);
    }

    @GetMapping("/api/export/issues/{projectId}")
    public ResponseEntity<byte[]> exportIssueToExcel(@PathVariable long projectId){
        try {
            String watermarkUrl = "https://firebasestorage.googleapis.com/v0/b/pms-fe88f.appspot.com/o/files%2FBlack%20and%20White%20Auto%20Repair%20Logo%20(1).png?alt=media&token=8d2ec209-7487-4938-ad06-0df66efeb240";
            byte[] excelFile = issueExportExcelService.exportIssuesToExcel(projectId, watermarkUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=issues.xls");
            return ResponseEntity.ok().headers(headers).body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/update/{projectId}")
    public String updateIssuesByProjectId(@PathVariable Long projectId, @RequestBody List<IssueUpdateDTO> issueUpdateDTOs){
        // Kiểm tra sự tồn tại của project
        Optional<Project> projectOptional = projectService.findById(projectId);
        if (projectOptional.isEmpty()) {
            return "Project not found";
        }

        Project project = projectOptional.get();
        StringBuilder resultMessage = new StringBuilder();

        // Duyệt qua từng IssueUpdateDTO trong danh sách
        for (IssueUpdateDTO issueUpdateDTO : issueUpdateDTOs) {
            // Tìm assignee theo email
            User assigneeOptional = userService.findByEmail(issueUpdateDTO.getAssigneeEmail());
            if (assigneeOptional == null) {
                resultMessage.append("Assignee not found for issue ID: ").append(issueUpdateDTO.getId()).append("\n");
                continue;
            }
            System.out.println(issueUpdateDTO.getAssigneeEmail());

            User assignee = assigneeOptional;

            // Tìm issue theo ID và project
            Optional<Issue> issueOptional = issueService.findByIdAndProject(issueUpdateDTO.getId(), project);
            if (issueOptional.isEmpty()) {
                resultMessage.append("Issue not found in this project for issue ID: ").append(issueUpdateDTO.getId()).append("\n");
                continue;
            }

            Issue issue = issueOptional.get();

            // Tìm UserIssueSalary cho assignee và issue
            Optional<UserIssueSalary> salaryOptional = userIssueSalaryRepository.findByUserAndIssue(assignee, issue);
            if (salaryOptional.isEmpty()) {
                resultMessage.append("Salary not found for issue ID: ").append(issueUpdateDTO.getId()).append("\n");
                continue;
            }

            UserIssueSalary salary = salaryOptional.get();

            // Cập nhật thông tin cho issue và salary
            issue.setTitle(issueUpdateDTO.getTitle());
            issue.setDescription(issueUpdateDTO.getDescription());
            issue.setStatus(issueUpdateDTO.getStatus());
            issue.setPriority(issueUpdateDTO.getPriority());
            issue.setStartDate(issueUpdateDTO.getStartDate());
            issue.setDueDate(issueUpdateDTO.getDueDate());
            issue.setPrice(issueUpdateDTO.getPrice());
            issue.setFinish(issueUpdateDTO.getFinish());
            issue.setAssignee(assignee);

            salary.setSalary(issueUpdateDTO.getSalary());
            salary.setCurrency(issueUpdateDTO.getCurrency());
            salary.setPaid(issueUpdateDTO.isPaid());

            // Lưu thông tin đã cập nhật
            issueRepository.save(issue);
            userIssueSalaryRepository.save(salary);

            resultMessage.append("Issue with ID: ").append(issueUpdateDTO.getId()).append(" updated successfully.\n");
        }

        // Trả về kết quả tổng hợp sau khi xử lý tất cả các issue
        return resultMessage.toString();
    }



}
