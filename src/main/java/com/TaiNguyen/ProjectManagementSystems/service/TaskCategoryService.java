package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.TaskCategory;
import com.TaiNguyen.ProjectManagementSystems.repository.TaskCategoryRepository;

import java.util.List;


public interface TaskCategoryService {

    public TaskCategory addTaskCategory(Long projectId, String label);

    public List<TaskCategory> getTaskCategoriesByProject(Project projectId);

    public void deleteTaskCategoryByProjectAndId(Long projectId, Long taskCategoryId);
}
