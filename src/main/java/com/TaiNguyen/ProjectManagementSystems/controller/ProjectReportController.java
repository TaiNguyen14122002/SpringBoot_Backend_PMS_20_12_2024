package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserProjectStartsDTO;
import com.TaiNguyen.ProjectManagementSystems.service.PdfExportService;
import com.TaiNguyen.ProjectManagementSystems.service.ProjectReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ProjectReportController {

    @Autowired
    private ProjectReportService projectReportService;

    @Autowired
    private PdfExportService pdfExportService;



    @GetMapping("/project/{projectId}")
    public ResponseEntity<byte[]> generatePorjectReport(@PathVariable Long projectId) {
        Project project = projectReportService.getProjectById(projectId);

        List<UserIssueSalary> salaries = projectReportService.getSalariesByProject(projectId);

        byte[] pdfData = pdfExportService.generateProjectReport(project, salaries);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=project_report_" + projectId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }


}
