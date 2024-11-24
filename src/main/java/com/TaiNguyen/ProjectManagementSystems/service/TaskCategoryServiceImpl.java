package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.TaskCategory;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.TaskCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskCategoryServiceImpl implements TaskCategoryService {

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;

    @Autowired
    private ProjectRepository projectRepository;


    @Override
    public TaskCategory addTaskCategory(Long projectId, String label) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setProject(project);
        taskCategory.setLabel(label);
        return taskCategoryRepository.save(taskCategory);
    }

    @Override
    public List<TaskCategory> getTaskCategoriesByProject(Project projectId) {
        return taskCategoryRepository.findByProject(projectId);
    }

    @Override
    public void deleteTaskCategoryByProjectAndId(Long projectId, Long taskCategoryId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        TaskCategory taskCategory = taskCategoryRepository.findByIdAndProject(taskCategoryId, project).orElseThrow(() -> new RuntimeException("Task category not found"));
        taskCategoryRepository.delete(taskCategory);
    }
}
