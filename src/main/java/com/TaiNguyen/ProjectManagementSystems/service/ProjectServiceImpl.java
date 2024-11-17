package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Chat;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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



        Project savedProject = projectRepository.save(createdProject);


        System.out.println("taiProject"+savedProject.getId());
        System.out.println("taiUser"+user.getId());

        workingTypeService.createWorkingType(user.getId(), savedProject.getId(), "Trực tuyến");


        Chat chat = new Chat();
        chat.setProject(savedProject);

        Chat projectChat = chatService.createChat(chat) ;
        savedProject.setChat(projectChat);
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

            else{if(project.getAction() != -1 )
            {
                project.setAction(action);
                projectRepository.save(project);
            }}

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


}
