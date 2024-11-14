package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.IssueDTO;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import com.TaiNguyen.ProjectManagementSystems.response.AuthResponse;
import com.TaiNguyen.ProjectManagementSystems.response.MessageResponse;
import com.TaiNguyen.ProjectManagementSystems.service.IssueService;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;
    @Autowired
    private IssueRepository issueRepository;

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

//    @GetMapping("/project/{projectId}/status")
//    public ResponseEntity<?> getIssuesCountByStatus(@RequestHeader("Authorization") String token,
//                                                    @PathVariable Long projectId) throws Exception {
//        User user = userService.findUserProfileByJwt(token);
//
//        boolean isOwner = issueService.checkProjectOwner(projectId, user.getId());
//        if(!isOwner){
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền truy cập thông tin này");
//        }
//
//        var issueConuts = issueService.getIssueCountByStatus(projectId);
//        return ResponseEntity.ok(issueConuts);
//    }

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


}
