package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.TaskCategory;
import com.TaiNguyen.ProjectManagementSystems.service.ProjectService;
import com.TaiNguyen.ProjectManagementSystems.service.TaskCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taskCategories")
public class TaskCategoryController {

    @Autowired
    private TaskCategoryService taskCategoryService;

    @Autowired
    private ProjectService projectService;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskCategory> addTaskCategory(@PathVariable Long projectId, @RequestParam String Label){
        TaskCategory taskCategory = taskCategoryService.addTaskCategory(projectId, Label);
        return ResponseEntity.ok(taskCategory);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskCategory> getTaskCategoriesByProject(@PathVariable Long projectId) throws Exception {
        Project project = projectService.getProjectById(projectId);

        if(project == null){
            throw new Exception("Project not found");
        }

        return taskCategoryService.getTaskCategoriesByProject(project);
    }

    @DeleteMapping("/project/{projectId}/task/{taskCategoryId}")
    public String deleteTaskCategory(@PathVariable Long projectId, @PathVariable Long taskCategoryId){
        taskCategoryService.deleteTaskCategoryByProjectAndId(projectId, taskCategoryId);
        return "Xoá TaskCategory với ID: " + taskCategoryId + " trong dự án " + projectId + " thành công.";
    }
}
