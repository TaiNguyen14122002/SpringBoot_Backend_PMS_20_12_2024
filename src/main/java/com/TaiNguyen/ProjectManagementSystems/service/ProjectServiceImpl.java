package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private WorkingTypeService workingTypeService;
    @Autowired
    private TaskCategoryRepository taskCategoryRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserIssueSalaryRepository userIssueSalaryRepository;

    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private EmailUtill emailUtill;

    @Autowired
    private WorkingTypeRepository workingTypeRepository;

    private void createDefaultTaskCategories(Project project){
        List<String> defaultLabel = List.of("Chưa làm", "Đang làm", "Hoàn thành");

        for(String label : defaultLabel){
            TaskCategory taskCategory = new TaskCategory();
            taskCategory.setLabel(label);
            taskCategory.setProject(project);
            taskCategoryRepository.save(taskCategory);
        }
    }



    @Override
    public Project createProject(Project project, User user) throws Exception {

        //Dat chu so huu cho du an
        project.setOwner(user);

        System.out.println("tai"+project.getTags());

        Project createdProject = new Project();

        createdProject.setOwner(user);
        createdProject.setTags(project.getTags());
        createdProject.setName(project.getName());
        createdProject.setCategory(project.getCategory());
        createdProject.setDescription(project.getDescription());
        createdProject.getTeam().add(user);
        createdProject.setFileNames(project.getFileNames());
        createdProject.setGoals(project.getGoals());
        createdProject.setEndDate(project.getEndDate());
        createdProject.setFundingAmount(project.getFundingAmount());



        Project savedProject = projectRepository.save(createdProject);


        System.out.println("taiProject"+savedProject.getId());
        System.out.println("taiUser"+user.getId());

        workingTypeService.createWorkingType(user.getId(), savedProject.getId(), "Trực tuyến");


        Chat chat = new Chat();
        chat.setProject(savedProject);

        Chat projectChat = chatService.createChat(chat) ;
        savedProject.setChat(projectChat);

        createDefaultTaskCategories(savedProject);

        return savedProject;
    }


    @Override
    public List<Project> getProjectByTeam(User user, String category, String tag) throws Exception {
        List<Project> projects;
        if (category != null && tag != null) {
            projects = projectRepository.findByTeamContainingOrOwner(user, user).stream()
                    .filter(project -> category.equals(project.getCategory()) && project.getTags().contains(tag))
                    .collect(Collectors.toList());
        } else if (category != null) {
            projects = projectRepository.findByTeamContainingOrOwner(user, user).stream()
                    .filter(project -> category.equals(project.getCategory()))
                    .collect(Collectors.toList());
        } else if (tag != null) {
            projects = projectRepository.findByTeamContainingOrOwner(user, user).stream()
                    .filter(project -> project.getTags().contains(tag))
                    .collect(Collectors.toList());
        } else {
            projects = projectRepository.findByTeamContainingOrOwner(user, user);
        }
//        System.out.println("Retrieved Projects: " + projects);
        return projects;
    }



    @Override
    public Project getProjectById(Long projectId) throws Exception {
        Optional<Project>optionalProject = projectRepository.findById(projectId);
        if(optionalProject.isEmpty()){
            throw new Exception("project not found");
        }
        return optionalProject.get();
    }

    @Override
    public void deleteProject(Long projectId, Long userId) throws Exception {

        getProjectById(projectId);
//        userService.findUserById(userId);
        projectRepository.deleteById(projectId);

    }

    @Override
    public Project updateStatusProject(String Status, Long id) throws Exception {
        Project project = getProjectById(id);

        project.setStatus(Status);

        return projectRepository.save(project);
    }

    @Override
    public void AddUserToProject(Long projectId, Long userId) throws Exception {

        Project project = getProjectById(projectId);
        User user = userService.findUserById(userId);
        if(!project.getTeam().contains(user)){
            project.getChat().getUsers().add(user);
            project.getTeam().add(user);

        }
        projectRepository.save(project);

    }

    @Override
    public void RemoveUserFromProject(Long projectId, Long userId) throws Exception {

        Project project = getProjectById(projectId);
        User user = userService.findUserById(userId);
        if(!project.getTeam().contains(user)){
            project.getChat().getUsers().remove(user);
            project.getTeam().remove(user);


        }
        projectRepository.save(project);

    }

    @Override
    public Chat getChatByProjectId(Long projectId) throws Exception {
        Project project = getProjectById(projectId);


        return project.getChat();
    }

    @Override
    public List<Project> searchProjects(String keyword, User user) throws Exception {

        return projectRepository.findByNameContainingAndTeamContains(keyword, user);
    }

    private void saveFilesToBase64(Project project, List<String> fileNames) throws Exception {
        for(String fileName : fileNames){
            Path path = Paths.get(fileName);
            byte[] fileBytes = Files.readAllBytes(path);
            String base64File = Base64.getEncoder().encodeToString(fileBytes);
            project.getFileNames().add(base64File);
        }
        projectRepository.save(project);
    }

    @Override
    public void uploadFileToProject(Long projectId, Project files) throws Exception {
        Project project = getProjectById(projectId);

        project.getFileNames().addAll(files.getFileNames());
        projectRepository.save(project);
    }

    @Override
    public long countTotalProjects(Long ownerId) {
        User owner = new User();
        owner.setId(ownerId);
        return projectRepository.countByOwner(owner);

    }

    @Override
    public long findParticipatedProjects(Long ownerId) {
        return projectRepository.findParticipatedProjects(ownerId);
    }

    @Override
    public Map<String, Map<String, Integer>> countUserProjectsInMonth(Long userId, int year) {
        List<Project> ownedProjects = projectRepository.findProjectsByOwnerInYear(userId, year);
        List<Project> participatedProjects = projectRepository.findProjectsByTeamMemberInYear(userId, year);

        // Khởi tạo map để chứa dữ liệu theo tháng
        Map<String, Map<String, Integer>> monthlyProjectCount = new HashMap<>();

        // Tạo danh sách tên các tháng
        String[] months = {"january", "february", "march", "april", "may", "june",
                "july", "august", "september", "october", "november", "december"};

        // Khởi tạo dữ liệu rỗng cho từng tháng
        for (String month : months) {
            Map<String, Integer> projectCount = new HashMap<>();
            projectCount.put("owned", 0);
            projectCount.put("participated", 0);
            projectCount.put("difference", 0);
            monthlyProjectCount.put(month, projectCount);
        }

        // Tính số lượng dự án sở hữu theo từng tháng
        for (Project project : ownedProjects) {
            String month = project.getCreatedDate().getMonth().toString().toLowerCase(); // Lấy tháng từ ngày tạo
            if (monthlyProjectCount.containsKey(month)) {
                monthlyProjectCount.get(month).put("owned", monthlyProjectCount.get(month).get("owned") + 1);
            }
        }

        // Tính số lượng dự án tham gia theo từng tháng
        for (Project project : participatedProjects) {
            String month = project.getCreatedDate().getMonth().toString().toLowerCase(); // Lấy tháng từ ngày tạo
            if (monthlyProjectCount.containsKey(month)) {
                monthlyProjectCount.get(month).put("difference", monthlyProjectCount.get(month).get("difference") + 1);
            }
        }

        // Tính sự chênh lệch giữa số dự án đã tạo và số dự án đã tham gia
        for (String month : months) {
            int owned = monthlyProjectCount.get(month).get("owned");
            int participated = monthlyProjectCount.get(month).get("difference");
            int difference = participated - owned; // Tính hiệu giữa số dự án đã tạo và đã tham gia
            monthlyProjectCount.get(month).put("participated", difference);
        }

        return monthlyProjectCount;
    }




    @Override
    public List<Project> countListUserProjectsInMonth(Long userId, int year) {
        List<Project> ownedProjects = projectRepository.findProjectsByOwnerInYear(userId, year);
        List<Project> participatedProjects = projectRepository.findProjectsByTeamMemberInYear(userId, year);

        // Tạo một danh sách để lưu trữ các dự án không trùng lặp
        List<Project> totalProjects = new ArrayList<>(ownedProjects);

        // Kiểm tra từng dự án trong danh sách participatedProjects và thêm vào nếu chưa tồn tại
        for (Project project : participatedProjects) {
            if (!totalProjects.contains(project)) {
                totalProjects.add(project);
            }
        }

        // Trả về danh sách các dự án không bị lặp
        return totalProjects;
    }

    @Override
    public List<Project> getPinnedProjects() {
        return projectRepository.findByAction(1);
    }

    @Override
    public List<Project> getDeletedProjects() {
        return projectRepository.findByAction(-1);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findByActionNot(0);
    }

    @Override
    public void updateProjectPinned(Long userId, Long projectId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Project project = projectRepository.findById(projectId).get();

        try {
            if (!project.getOwner().getEmail().equals(currentUsername)) {
                throw new RuntimeException("Chỉ chủ dự án mới có thể chỉnh sửa");
            }

            else{if(project.getAction() == 0 )
            {
                project.setAction(1);
                projectRepository.save(project);
            }}

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateProjectDeleted(Long userId, Long projectId, int action) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Project project = projectRepository.findById(projectId).get();

        try {
            if (!project.getOwner().getEmail().equals(currentUsername)) {
                throw new RuntimeException("Chỉ chủ dự án mới có thể chỉnh sửa");
            }

                project.setAction(action);
                projectRepository.save(project);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Project> getProjectPinned(Long userId, String category, String tag) {
        List<Project> projects;
        if (category != null && tag != null) {
            projects = projectRepository.findProjectPinnedByUser(userId).stream()
                    .filter(project -> category.equals(project.getCategory()) && project.getTags().contains(tag))
                    .collect(Collectors.toList());
        } else if (category != null) {
            projects = projectRepository.findProjectPinnedByUser(userId).stream()
                    .filter(project -> category.equals(project.getCategory()))
                    .collect(Collectors.toList());
        } else if (tag != null) {
            projects = projectRepository.findProjectPinnedByUser(userId).stream()
                    .filter(project -> project.getTags().contains(tag))
                    .collect(Collectors.toList());
        } else {
            projects = projectRepository.findProjectPinnedByUser(userId);
        }

        return projects;
    }

    @Override
    public List<Project> getExpiredProjectsByUser(User user) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextWeekDate = currentDate.plusDays(30);
        return projectRepository.findExpiringProjectsByUser(user, currentDate, nextWeekDate);
    }

    @Override
    public List<Project> getExpiredProjects(User user) {
        LocalDate currentDate = LocalDate.now();
        return projectRepository.findExpiredProjects(currentDate);
    }

    @Override
    public List<Project> getDeletedProjectsByOwner(Long userId) {
        return projectRepository.findByOwnerIdAndAction(userId, -1);
    }

    @Override
    public void updateStatus(Long projectId, String newStatus) {
        Project project = projectRepository.findById(projectId).get();

        project.setStatus(newStatus);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDetailsDTO> getProjectByOwner(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Project> projects = projectRepository.findByOwnerId(userId);

        return projects.stream().map(project -> {
            List<String> teamMembers = project.getTeam().stream()
                    .map(member -> member.getFullname())
                    .collect(Collectors.toList());

            List<String> issues = project.getIssues().stream()
                    .map(issue -> issue.getTitle())
                    .collect(Collectors.toList());

            return new ProjectDetailsDTO(
                    project.getId(),
                    project.getName(),
                    teamMembers,
                    issues,
                    project.getFundingAmount(),
                    project.getProfitAmount(),
                    project.getStatus()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public void updateProfitAmount(long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if(projectOptional.isPresent()) {
            Project project = projectOptional.get();

            List<Issue> issues = project.getIssues();

            BigDecimal totalSalary = BigDecimal.ZERO;

            for(Issue issue : issues) {
                List<UserIssueSalary> salaries = issue.getSalaries();
                for(UserIssueSalary salary : salaries) {
                    totalSalary = totalSalary.add(salary.getSalary());
                }
            }
            System.out.println("Family"+totalSalary);
            BigDecimal profitAmount = project.getFundingAmount().subtract(totalSalary);
            project.setProfitAmount(profitAmount);
            projectRepository.save(project);
        }else {
            throw new RuntimeException("Project not found");
        }
    }

    @Transactional
    @Override
    public void deleteFileName(Long projectId, String fileName) {
        databaseService.enableSafeMode();
        projectRepository.deleteFileNameFromProject(projectId, fileName);
    }

    @Override
    public List<IssueSalaryDTO> getIssueAndSalariesByProjectId(Long projectId) {
        List<Issue> issues = issueRepository.findByProjectId(projectId);
        List<IssueSalaryDTO> result = new ArrayList<>();
        for(Issue issue : issues) {
            List<UserIssueSalary> salaries = userIssueSalaryRepository.findByIssue(issue);
            result.add(new IssueSalaryDTO(issue, salaries));
        }
        return result;
    }

    @Override
    public List<Project> getProjectsByOwnerAndAction(User owner) {
        return projectRepository.findProjectsByOwner(owner);
    }

    @Override
    public ProjectDetailsResponse getProjectDetailsByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        // Lấy danh sách thành viên của dự án và tính toán thông tin
        List<TeamMemberResponse> teamMembers = project.getIssues().stream()
                .map(issue -> issue.getAssignee()) // Lấy người được giao nhiệm vụ
                .distinct() // Loại bỏ trùng lặp người dùng
                .map(user -> {
                    // Lọc nhiệm vụ của người dùng trong dự án hiện tại
                    List<IssueDetailsResponse> issues = user.getAssignedIssues().stream()
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
                                            .orElse("0"), // Mặc định nếu không tìm thấy
                                    issue.getActualDate(), // Thêm thời gian thực tế (actualDate)
                                    issue.getDueDate(), // Thêm thời gian dự kiến (dueDate)
                                    issue.getFinish() // Thêm đánh giá chất lượng (finish)
                            ))
                            .collect(Collectors.toList());


                    // Tính toán các thông tin bổ sung
                    long totalAssignedIssues = user.getAssignedIssues().stream()
                            .filter(issue -> issue.getProject().getId() == projectId)
                            .count();

                    long totalInProgressIssues = user.getAssignedIssues().stream()
                            .filter(issue -> issue.getProject().getId() == projectId)
                            .filter(issue -> issue.getStatus().equals("In Progress"))
                            .count();

                    long totalPendingIssues = user.getAssignedIssues().stream()
                            .filter(issue -> issue.getProject().getId() == projectId)
                            .filter(issue -> issue.getStatus().equals("Pending"))
                            .count();

                    double averagePriority = user.getAssignedIssues().stream()
                            .filter(issue -> issue.getProject().getId() == projectId)
                            .mapToInt(issue -> {
                                String priority = issue.getPriority();
                                // Chuyển đổi "priority" từ chuỗi sang số
                                switch (priority.toLowerCase()) {
                                    case "Low":
                                        return 1;
                                    case "Medium":
                                        return 2;
                                    case "High":
                                        return 3;
                                    default:
                                        return 0; // Giá trị mặc định nếu không khớp
                                }
                            })
                            .average()
                            .orElse(0.0);

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
                            totalSalaryIssue, // Tổng lương của các nhiệm vụ thuộc dự án
                            totalAssignedIssues, // Tổng số nhiệm vụ được giao
                            totalInProgressIssues, // Tổng số nhiệm vụ đang làm
                            totalPendingIssues, // Tổng số nhiệm vụ chưa làm
                            averagePriority // Trung bình độ ưu tiên
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

    @Override
    public Optional<Project> updateEndDate(long projectId, LocalDate endDate) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if(projectOpt.isPresent()){
            Project project = projectOpt.get();
            project.setEndDate(endDate);
            return Optional.of(projectRepository.save(project));
        }
        return Optional.empty();
    }

    @Override
    public Project updateProject(long id, Project project) {
        System.out.println("Dữ liệu nhận được từ frontend: " + project);
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            Project existingProject = projectOptional.get();

            // Cập nhật các trường của dự án
            if (project.getName() != null && !project.getName().equals(existingProject.getName())) {
                existingProject.setName(project.getName());
            }
            if (project.getDescription() != null && !project.getDescription().equals(existingProject.getDescription())) {
                existingProject.setDescription(project.getDescription());
            }
            if (project.getGoals() != null && !project.getGoals().equals(existingProject.getGoals())) {
                existingProject.setGoals(project.getGoals());
            }
            if (project.getEndDate() != null && !project.getEndDate().equals(existingProject.getEndDate())) {
                existingProject.setEndDate(project.getEndDate());
            }
            return projectRepository.save(existingProject);
        } else {
            throw new RuntimeException("Project not found with id: " + id);
        }
    }

    @Override
    public Optional<Project> findById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    @Transactional
    public void removeUserFromProject(long projectId, long userId, long currentUserId) throws MessagingException {
        // Tìm dự án
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        // Kiểm tra quyền chủ dự án
        if (project.getOwner() == null || project.getOwner().getId() != currentUserId) {
            throw new RuntimeException("Chỉ chủ dự án mới có thể xoá thành viên");
        }

        // Tìm người dùng
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Xóa người dùng khỏi working type repository
        workingTypeRepository.deleteByUserIdAndProjectId(userId, projectId);

        // Sử dụng Iterator để xóa người dùng khỏi team
        Iterator<User> iterator = project.getTeam().iterator();
        while (iterator.hasNext()) {
            User teamMember = iterator.next();
            if (teamMember.getId() == userId) {
                iterator.remove();  // Xóa người dùng khỏi team
                break;  // Dừng khi đã tìm thấy người dùng cần xóa
            }
        }

        // Xử lý assignee trong các issues
        for (Issue issue : project.getIssues()) {
            if (issue.getAssignee() != null && issue.getAssignee().getId() == userId) {
                issue.setAssignee(null);  // Gỡ assignee khỏi issue
                issueRepository.save(issue);  // Lưu issue đã cập nhật
                System.out.println("Done");
            }
        }

        // Lưu lại dự án sau khi thay đổi
        projectRepository.save(project);

        // Gửi email thông báo
        String subject = "Thông báo: Bạn đã bị xoá khỏi dự án";
        String htmlContent =
                "<!DOCTYPE html>" +
                        "<html lang=\"vi\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                        "<title>Thông báo quan trọng</title>" +
                        "</head>" +
                        "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">" +
                        "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4;\">" +
                        "  <div style=\"background-color: #ffffff; padding: 30px; border-radius: 5px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\">" +
                        "    <div style=\"text-align: center; margin-bottom: 20px; border-bottom: 2px solid #3498db; padding-bottom: 20px;\">" +
                        "      <h1 style=\"color: #3498db; font-size: 24px; margin: 0;\">Thông báo quan trọng</h1>" +
                        "    </div>" +
                        "    <p style=\"font-size: 16px; line-height: 1.5; color: #333;\">Kính gửi " + user.getFullname() + ",</p>" +
                        "    <p style=\"font-size: 16px; line-height: 1.5; color: #333;\">Chúng tôi xin thông báo rằng bạn đã được xóa khỏi dự án:</p>" +
                        "    <p style=\"font-size: 18px; font-weight: bold; color: #e74c3c; text-align: center; margin: 20px 0; padding: 10px; background-color: #fadbd8; border-radius: 3px;\">" +
                        project.getName() + "</p>" +
                        "    <p style=\"font-size: 16px; line-height: 1.5; color: #333;\">Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chủ dự án để biết thêm thông tin.</p>" +
                        "    <p style=\"font-size: 16px; line-height: 1.5; color: #333; margin-top: 30px;\">Trân trọng,<br />Đội ngũ Quản lý Dự án</p>" +
                        "  </div>" +
                        "  <div style=\"text-align: center; margin-top: 20px; font-size: 14px; color: #888;\">" +
                        "    © 2023 Project Management Team. All rights reserved." +
                        "  </div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";

        // Gửi email
        emailUtill.sendEmail(user.getEmail(), subject, htmlContent);
    }



}
