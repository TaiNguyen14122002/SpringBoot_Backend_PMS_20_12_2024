package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService{
    @Override
    public ByteArrayOutputStream exportIssuesToExcel(List<Project> projects) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Issues");

        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "Project ID", "Project Name", "Project Owner", "Issue ID", "Issue Title",
                "Assignee", "Priority", "Status", "Due Date"
        };
        for( int i =0; i<columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for(Project project : projects){
            for(Issue issue : project.getIssues()){
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(project.getId());  // ID dự án
                row.createCell(1).setCellValue(project.getName());  // Tên dự án
                User owner = project.getOwner();
                row.createCell(2).setCellValue(owner != null ? owner.getFullname() : "");  // Người tạo dự án

                row.createCell(3).setCellValue(issue.getId());  // ID tác vụ
                row.createCell(4).setCellValue(issue.getTitle());  // Tên tác vụ

                User assignee = issue.getAssignee();
                row.createCell(5).setCellValue(assignee != null ? assignee.getFullname() : "");  // Người được phân công tác vụ

                row.createCell(6).setCellValue(issue.getPriority());  // Độ ưu tiên
                row.createCell(7).setCellValue(issue.getStatus());  // Trạng thái
                row.createCell(8).setCellValue(issue.getDueDate() != null ? issue.getDueDate().toString() : "");  // Ngày hết hạn
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return byteArrayOutputStream;
    }
}
