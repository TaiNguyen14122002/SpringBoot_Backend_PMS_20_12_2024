package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;

import java.util.List;

public interface ProjectReportService {

    public Project getProjectById(Long projectId);

    public List<UserIssueSalary> getSalariesByProject(Long projectId);
}
