package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {

    List<TaskCategory> findByProject(Project projectId);

    Optional<TaskCategory> findByIdAndProject(Long taskCategoryId, Project projectId);
}
