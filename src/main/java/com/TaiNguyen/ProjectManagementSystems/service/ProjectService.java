package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Chat;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;

import java.util.List;

public interface ProjectService {

    Project createProject(Project project, User user)throws Exception;


    List<Project> getProjectByTeam(User user, String category, String tag)throws Exception;

    Project getProjectById(Long projectId)throws Exception;

    void deleteProject(Long projectId, Long userId)throws Exception;

    Project updateProject(Project UpdateProject, Long id)throws Exception;

    void AddUserToProject(Long projectId, Long userId)throws Exception;

    void RemoveUserFromProject(Long projectId, Long userId)throws Exception;

    Chat getChatByProjectId(Long projectId)throws Exception;

    List<Project> searchProjects(String keyword, User user) throws Exception;
}
