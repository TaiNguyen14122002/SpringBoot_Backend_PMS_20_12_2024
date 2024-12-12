package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
public class IssueExportExcelService {
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;

    public IssueExportExcelService(ProjectRepository projectRepository, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
    }

    public byte[] exportIssuesToExcel(long projectId, String watermarkUrl) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IOException("Project not found"));
        List<Issue> issues = issueRepository.findByProjectId(project.getId());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Issues");
            createHeaderRow(sheet);

            int rowCount = 1;
            for (Issue issue : issues) {
                Row row = sheet.createRow(rowCount++);
                writeIssueData(issue, row);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi chi tiết để kiểm tra nguyên nhân
            throw new RuntimeException("Error while generating Excel file", e);
        }
        return outputStream.toByteArray();
    }
    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("id");
        headerRow.createCell(1).setCellValue("title");
        headerRow.createCell(2).setCellValue("description");
        headerRow.createCell(3).setCellValue("status");
        headerRow.createCell(4).setCellValue("priority");
        headerRow.createCell(5).setCellValue("startDate");
        headerRow.createCell(6).setCellValue("dueDate");
        headerRow.createCell(7).setCellValue("price");
        headerRow.createCell(8).setCellValue("finish");
        headerRow.createCell(9).setCellValue("assigneeName");
        headerRow.createCell(10).setCellValue("assigneeEmail");
        headerRow.createCell(11).setCellValue("salary");
        headerRow.createCell(12).setCellValue("currency");
        headerRow.createCell(13).setCellValue("isPaid");
    }

    private void writeIssueData(Issue issue, Row row) {
        row.createCell(0).setCellValue(issue.getId());
        row.createCell(1).setCellValue(issue.getTitle());
        row.createCell(2).setCellValue(issue.getDescription());
        row.createCell(3).setCellValue(issue.getStatus());
        row.createCell(4).setCellValue(issue.getPriority());
        row.createCell(5).setCellValue(issue.getStartDate().toString());
        row.createCell(6).setCellValue(issue.getDueDate().toString());
        row.createCell(7).setCellValue(issue.getPrice());
        row.createCell(8).setCellValue(issue.getFinish());

        if (issue.getAssignee() != null) {
            row.createCell(9).setCellValue(issue.getAssignee().getFullname());
            row.createCell(10).setCellValue(issue.getAssignee().getEmail());
        }

        List<UserIssueSalary> salaries = issue.getSalaries();
        if (!salaries.isEmpty()) {
            UserIssueSalary salary = salaries.get(0);
            row.createCell(11).setCellValue(salary.getSalary().doubleValue());
            row.createCell(12).setCellValue(salary.getCurrency());
            row.createCell(13).setCellValue(salary.isPaid() ? "True" : "False");
        }
    }


}
