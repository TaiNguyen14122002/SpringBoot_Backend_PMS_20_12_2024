package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.service.ExcelExportService;
import com.TaiNguyen.ProjectManagementSystems.service.PdfService;
import com.TaiNguyen.ProjectManagementSystems.service.ProjectService;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/projects/{projectId}/issues/export")
    public ResponseEntity<byte[]> exportToExcel(@RequestHeader("Authorization") String jwt, @PathVariable long projectId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if(user != null){
            Project project = projectService.getProjectById(projectId);
            List<Project> projects = List.of(project);

            byte[] excelFile = excelExportService.exportIssuesToExcel(projects).toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=issues_report.xlsx"); // Đặt tên file khi tải về
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Đảm bảo loại tệp là Excel

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

//    public ResponseEntity<byte[]> getUserTasksReport(@PathVariable long userID) throws Exception {
//        List<Issue> issues = projectService.
//    }
}
