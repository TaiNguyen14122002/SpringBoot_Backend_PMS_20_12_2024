package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.repository.WorkingTypeRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelExportServiceImpl implements ExcelExportService{

    @Autowired
    private WorkingTypeRepository workingTypeRepository;

    @Override
    public ByteArrayOutputStream exportIssuesToExcel(List<Project> projects) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Issues");

        Row headerRow = sheet.createRow(0);
        String[] columns = {
                "ID Dự án", "Tên dự án", "Người tạo dự án", "ID Tác vụ", "Tên tác vụ",
                "Người được giao", "Độ ưu tiên", "Trạng thái", "Ngày hết hạn", "Hình thức làm việc", "Đã hoàn thành", "Lương tác vụ", "Thực hưởng", "Thanh toán"
        };
        for( int i =0; i<columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for(Project project : projects){
            List<User> usersInProject = getUserInProject(project);
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

                    String workingType = getWorkingTypeForUserAndProject(assignee, project);
                    row.createCell(9).setCellValue(workingType);

                    row.createCell(10).setCellValue(issue.getFinish() != null ? issue.getFinish() + "%" : "");  // Mức độ hoàn thành

                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    row.createCell(11).setCellValue(issue.getPrice() != null ? currencyFormat.format(Double.parseDouble(issue.getPrice())) : "");

                    BigDecimal salary = getUserSalaryForIssue(assignee, issue);
                    if(salary != null){
                        NumberFormat currencyFormat2 = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        row.createCell(12).setCellValue(currencyFormat2.format(salary));
                    }else{
                        row.createCell(12).setCellValue("Không có ");
                    }

                    Boolean isPaid = getPaymentStatusForIssue(assignee, issue);
                    row.createCell(13).setCellValue(isPaid != null && isPaid ? "Đã thanh toán" : "Chưa thanh toán");
                }

            }


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return byteArrayOutputStream;
    }

    private String getWorkingTypeForUserAndProject(User assignee, Project project) {
        if(assignee != null && project != null){
            WorkingType workingType = workingTypeRepository.findByUserIdAndProjectId(assignee.getId(), project.getId());
            return workingType != null ? workingType.getWorkType() : "";
        }
        return "";
    }

    private List<User> getUserInProject(Project project) {
        return project.getTeam();
    }

    private BigDecimal getUserSalaryForIssue(User user, Issue issue){
        for(UserIssueSalary userIssueSalary : issue.getSalaries()){
            if(userIssueSalary.getUser().equals(user)){
                return userIssueSalary.getSalary();
            }
        }
        return null;
    }

    private Boolean getPaymentStatusForIssue(User user, Issue issue){
        for(UserIssueSalary userIssueSalary : issue.getSalaries()){
            if(userIssueSalary.getUser().equals(user)){
                return userIssueSalary.isPaid();
            }
        }
        return false;
    }


}
