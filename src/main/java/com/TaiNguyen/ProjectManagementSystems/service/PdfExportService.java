package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] generateProjectReport(Project project, List<UserIssueSalary> salaries){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try(PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument)) {


            document.add(new Paragraph("Báo cáo dự án:" + project.getName()).setBold().setFontSize(16));
            document.add(new Paragraph("Mô tả: " + project.getDescription()));
            document.add(new Paragraph("Danh sách thành viên và lương: ").setBold());

            Table table = new Table(new float[]{3,3,3,3});
            table.addHeaderCell("Thành viên");
            table.addHeaderCell("Nhiệm vụ");
            table.addHeaderCell("Lương");
            table.addHeaderCell("Trạng thái");

            BigDecimal totalSalary = BigDecimal.ZERO;

            for(UserIssueSalary salary : salaries){
                table.addCell(salary.getUser().getFullname());
                table.addCell(salary.getIssue().getTitle());
                table.addCell(salary.getSalary() + " " + salary.getIssue().getDescription());
                table.addCell(salary.isPaid() ? "Đã thanh toán" : "Chưa thanh toán");

                totalSalary = totalSalary.add(salary.getSalary());
            }
            document.add(table);
            document.add(new Paragraph("\nTổng số thành viên: " + salaries.size()));
            document.add(new Paragraph("Tổng lương: " + totalSalary + " VND").setBold());

        }catch (Exception e){
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
