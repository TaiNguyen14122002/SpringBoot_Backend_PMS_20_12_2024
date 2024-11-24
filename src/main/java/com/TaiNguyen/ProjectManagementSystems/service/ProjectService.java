package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProjectService {

    Project createProject(Project project, User user)throws Exception;


    List<Project> getProjectByTeam(User user, String category, String tag)throws Exception;


    Project getProjectById(Long projectId)throws Exception;

    void deleteProject(Long projectId, Long userId)throws Exception;

    Project updateStatusProject(String Status, Long id)throws Exception;

    void AddUserToProject(Long projectId, Long userId)throws Exception;

    void RemoveUserFromProject(Long projectId, Long userId)throws Exception;

    Chat getChatByProjectId(Long projectId)throws Exception;

    List<Project> searchProjects(String keyword, User user) throws Exception;

    void uploadFileToProject(Long projectId, Project file) throws Exception;

    long countTotalProjects(Long ownerId);

    long findParticipatedProjects(Long ownerId);

    Map<String, Map<String, Integer>> countUserProjectsInMonth(Long userId, int year);


    List<Project> countListUserProjectsInMonth(Long userId, int year);


    public List<Project> getPinnedProjects();
    public List<Project> getDeletedProjects();
    public List<Project> getAllProjects();

    public void updateProjectPinned(Long userId, Long projectId);

    public void updateProjectDeleted(Long userId, Long projectId, int action);

    public List<Project> getProjectPinned(Long userId, String category, String tag);

    public List<Project> getExpiredProjectsByUser(User user);

    public List<Project> getExpiredProjects(User user);

    public List<Project> getDeletedProjectsByOwner(Long userId);

    public void updateStatus(Long projectId, String newStatus);

    public List<ProjectDetailsDTO> getProjectByOwner(Long userId);

    public void updateProfitAmount(long projectId);

    public void deleteFileName(Long projectId, String fileName);

    public List<IssueSalaryDTO> getIssueAndSalariesByProjectId(Long projectId);

    public List<Project> getProjectsByOwnerAndAction(User owner);

    public ProjectDetailsResponse getProjectDetailsByProjectId(Long projectId);

}
