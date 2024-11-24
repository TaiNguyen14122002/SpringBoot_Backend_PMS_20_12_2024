package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserIssueSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectReportServiceImpl implements ProjectReportService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserIssueSalaryRepository userIssueSalaryRepository;

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public List<UserIssueSalary> getSalariesByProject(Long projectId) {
        return userIssueSalaryRepository.findAllByIssue_Project_Id(projectId);
    }
}
