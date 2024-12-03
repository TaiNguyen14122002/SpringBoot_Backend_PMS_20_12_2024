package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.repository.InviteRequest;
import com.TaiNguyen.ProjectManagementSystems.repository.NotificationRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.WorkingTypeRepository;
import com.TaiNguyen.ProjectManagementSystems.response.ErrorResponse;
import com.TaiNguyen.ProjectManagementSystems.response.MessageResponse;
import com.TaiNguyen.ProjectManagementSystems.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WorkingTypeService workingTypeService;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DatabaseService databaseService;

    @PutMapping("/uploadFileToProject/{projectId}/upload")
    public String uploadFile(@PathVariable Long projectId, @RequestBody Project files) throws Exception {
        try {
            projectService.uploadFileToProject(projectId, files);
            return "File uploaded successfully";
        }catch (Exception e){
            return "Error occured while uploading file" + e.getMessage();
        }
    }


    @GetMapping()
    public ResponseEntity<List<Project>>getProjects(
            @RequestParam(required = false)String category,
            @RequestParam(required = false)String tag,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            List<Project> projects= projectService.getProjectByTeam(user, category, tag);

            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
    }
    @PostMapping()
    public ResponseEntity<Project>createProject(

            @RequestHeader("Authorization") String jwt,
            @RequestBody Project project
    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            System.out.println("Created Project ID: " + user.getId());
            Project createdProject= projectService.createProject(project, user);
            String notificationContent = "Bạn đã thêm một dự án mới \"" + createdProject.getName() +  "\" đã được tạo!";
            notificationService.createNotification(notificationContent, createdProject, null);
            return new ResponseEntity<>(createdProject, HttpStatus.OK);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project>getProjectById(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            Project project= projectService.getProjectById(projectId);
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
    }



    @PutMapping("/{projectId}/status")
    public ResponseEntity<Project>updateProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt,
            @RequestParam String status
    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            Project updatedProject= projectService.updateStatusProject(status, projectId);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<MessageResponse>deleteProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt

    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            projectService.deleteProject(projectId, user.getId());
            notificationService.deleteNotification(projectId);
            MessageResponse res = new MessageResponse("Xoá thành công dự án");
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Project>>searchProjects(
            @RequestParam(required = false)String keyword,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            List<Project> projects= projectService.searchProjects(keyword, user);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
    }


    @GetMapping("/{projectId}/chat")
    public ResponseEntity<Chat>getChatByProjectId(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        {
            User user = userService.findUserProfileByJwt(jwt);
            Chat chat= projectService.getChatByProjectId(projectId);
            return new ResponseEntity<>(chat, HttpStatus.OK);
        }
    }

    @PostMapping("/invite")
    public ResponseEntity<MessageResponse>inviteProject(
            @RequestBody InviteRequest req,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        {
            User user = userService.findUserByEmail(req.getEmail());
            Project project = projectService.getProjectById(req.getProjectId());
            if(user == null){
                MessageResponse error = new MessageResponse("Người dùng không tồn tại trong hệ thống");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            invitationService.sendInvitation(req.getEmail(), req.getProjectId(), project.getName());
            MessageResponse res = new MessageResponse("Đã gửi mail yêu cầu tham gia dự án");
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }

    @GetMapping("/accept_invitation")
    public ResponseEntity<Invitation> acceptInviteProject(
            @RequestParam String token,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Invitation invitation = invitationService.acceptInvitation(token, user.getId());
        projectService.AddUserToProject(invitation.getProjectId(), user.getId());
        workingTypeService.createWorkingType(user.getId(), invitation.getProjectId(), "Trong công ty");

        return new ResponseEntity<>(invitation, HttpStatus.ACCEPTED);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalProjects(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        long totalProjects = projectService.countTotalProjects(user.getId());
        return ResponseEntity.ok(totalProjects);
    }

    @GetMapping("/countProjectJoin")
    public ResponseEntity<Long> getCountJoinProject(@RequestHeader("Authorization") String jwt) throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        long JointotalProjects = projectService.findParticipatedProjects(user.getId());
        return ResponseEntity.ok(JointotalProjects);
    }

    @GetMapping("/countProjects")
    public Map<String, Map<String, Integer>> countProjectsInMonth(@RequestParam int year,
                                                                  @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        return projectService.countUserProjectsInMonth(user.getId(), year);
    }

    @GetMapping("/count/ListProject")
    public List<Project> countListProjectsInMonth(@RequestParam int year,
                                              @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        return projectService.countListUserProjectsInMonth(user.getId(), year);
    }

    @GetMapping("/pinned")
    public List<Project> getPinnedProjects() {
        return projectService.getPinnedProjects();
    }

    @GetMapping("/deleted")
    public List<Project> getDeletedProjects() {
        return projectService.getDeletedProjects();
    }

    @GetMapping("/all")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PutMapping("/{projectId}/update-action")
    public String updateProjectPinned(@RequestHeader("Authorization") String jwt, @PathVariable Long projectId ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        try {
            projectService.updateProjectPinned(user.getId(), projectId);
            return "Ghim thành công dự án";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @GetMapping("/projectPinned")
    public ResponseEntity<List<Project>> getProjectPinnedByUser( @RequestHeader("Authorization") String jwt,
                                                                 @RequestParam(required = false)String category,
                                                                 @RequestParam(required = false)String tag) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projectPinned = projectService.getProjectPinned(user.getId(), category, tag);
        return ResponseEntity.ok(projectPinned);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Project>> getExpiringProjects(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        List<Project> expiringProjects = projectService.getExpiredProjectsByUser(user);
        return ResponseEntity.ok(expiringProjects);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Project>> getExpiredProjectsByUser(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Project> expiringProjects = projectService.getExpiredProjects(user);
        return ResponseEntity.ok(expiringProjects);
    }

    @PutMapping("/{projectId}/update-action-deleted")
    public String updateProjectDeleted(@RequestHeader("Authorization") String jwt, @PathVariable Long projectId, @RequestParam int action ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        try {
            projectService.updateProjectDeleted(user.getId(), projectId, action);
            return "Xoá thành công dự án";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @GetMapping("/Projectsdeleted")
    public ResponseEntity<List<Project>> getDeletedProjectsByOwner(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Project> deletedProjects = projectService.getDeletedProjectsByOwner(user.getId());

        if(deletedProjects.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(deletedProjects);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Project>> getOwnedProjectsByUser(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        try {
            List<Project> ownedProjects = projectRepository.findByOwnerId(user.getId());
            return ResponseEntity.ok(ownedProjects);

        }catch(Exception e){
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/owner/statistical")
    public List<ProjectDetailsDTO> getProjectSByOwner(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return projectService.getProjectByOwner(user.getId());
    }

    @PutMapping("/{projectId}/update-profit")
    public ResponseEntity<String> updateProfitAmount(@PathVariable long projectId){
        try {
            projectService.updateProfitAmount(projectId);
            return ResponseEntity.ok("Profit amount updated successfully.");

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{projectId}/deleteFileName")
    public ResponseEntity<String> deleteFileName(
            @PathVariable Long projectId,
            @RequestParam String fileName) {
        try {
            projectService.deleteFileName(projectId, fileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting file");
        }
    }

    @GetMapping("/{projectId}/Export/PDF")
    public ResponseEntity<Map<String, Object>> getProjectDetails(@PathVariable Long projectId) throws Exception {
        Project project = projectService.getProjectById(projectId);

        List<IssueSalaryDTO> issueSalaries = projectService.getIssueAndSalariesByProjectId(project.getId());

        Map<String, Object> response = Map.of(
                "project", project,
                "issueWithSalaries", issueSalaries
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Members/{projectId}")
    public ProjectUserDTO getProjectWithMembers(@PathVariable long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        List<UserDTO> teamMembers = project.getTeam().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getFullname(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getCompany(),
                        user.getProgramerposition(),
                        user.getCreatedDate(),
                        user.getAvatar()
                )).collect(Collectors.toList());

        return new ProjectUserDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCategory(),
                project.getTags(),
                project.getFileNames(),
                project.getGoals(),
                project.getCreatedDate(),
                project.getEndDate(),
                project.getStatus(),
                project.getFundingAmount(),
                project.getProfitAmount(),
                teamMembers

        );
    }

    @GetMapping("/owner/action")
    public List<Project> getProjectsByOwner(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if(user == null){
            throw new RuntimeException("User not found");
        }
        return projectService.getProjectsByOwnerAndAction(user);
    }

    @GetMapping("/{projectId}/detailsMembers")
    public ProjectDetailsResponse getProjectDetailsMembers(@PathVariable Long projectId) {
        // Lấy thông tin dự án
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Lấy danh sách thành viên của dự án và tính toán thông tin
        List<TeamMemberResponse> teamMembers = project.getIssues().stream()
                .map(issue -> issue.getAssignee()) // Lấy người được giao nhiệm vụ
                .distinct() // Loại bỏ trùng lặp người dùng
                .map(user -> {
                    // Lọc nhiệm vụ của người dùng trong dự án hiện tại
                    List<IssueDetailsResponse> issues = user.getAssignedIssues() .stream()
                            .filter(issue -> issue.getProject().getId() == projectId) // Lọc nhiệm vụ thuộc dự án
                            .map(issue -> new IssueDetailsResponse(
                                    issue.getId(),
                                    issue.getTitle(),
                                    issue.getDescription(),
                                    issue.getStatus(),
                                    issue.getPriority(),
                                    String.join(", ", issue.getTags()),
                                    issue.getSalaries().stream()
                                            .filter(s -> s.getUser().equals(user)) // Lọc `UserId`
                                            .map(s -> s.getSalary().toString())
                                            .findFirst()
                                            .orElse("0") // Mặc định nếu không tìm thấy
                            ))
                            .collect(Collectors.toList());

                    // Tính tổng thực hưởng `salary` cho các Issue của User trong dự án
                    BigDecimal totalSalaryIssue = user.getSalaries().stream()
                            .filter(s -> s.getIssue().getProject().getId() == projectId) // Lọc `ProjectId`
                            .map(UserIssueSalary::getSalary)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Trả về thông tin thành viên
                    return new TeamMemberResponse(
                            user.getId(),
                            user.getFullname(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getCompany(),
                            user.getProgramerposition(),
                            user.getCreatedDate(),
                            user.getAvatar(),
                            issues,
                            totalSalaryIssue // Tổng lương của các nhiệm vụ thuộc dự án
                    );
                }).collect(Collectors.toList());

        // Trả về thông tin chi tiết dự án
        return new ProjectDetailsResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCategory(),
                project.getTags(),
                project.getFileNames(),
                project.getGoals(),
                project.getCreatedDate(),
                project.getEndDate(),
                project.getStatus(),
                project.getFundingAmount(),
                project.getProfitAmount(),
                teamMembers
        );
    }

    @PutMapping("/{projectId}/endDate")
    public ResponseEntity<Project> updateEndDate(@PathVariable long projectId, @RequestBody LocalDate endDate){
        Optional<Project> updatedProject = projectService.updateEndDate(projectId, endDate);
        return updatedProject.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/updateProject/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable long projectId, @RequestBody Project project){
        Project updatedProject = projectService.updateProject(projectId, project);
        return ResponseEntity.ok(updatedProject);
    }





}
