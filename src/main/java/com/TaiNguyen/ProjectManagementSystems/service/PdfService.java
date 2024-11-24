package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PdfService {
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public PdfService(IssueRepository issueRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<byte[]> generateTasksPdf(long projectId, long userId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Set page margins
        document.setMargins(50, 50, 50, 50);

        // Create fonts
        PdfFont font = PdfFontFactory.createFont("Helvetica");
        PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont italicFont = PdfFontFactory.createFont("Helvetica-Oblique");

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Issue> issues = issueRepository.findByProject_IdAndAssignee_Id(projectId, userId);

        // Add logo (replace with your actual logo path)
//        Image logo = new Image(ImageDataFactory.create("path/to/your/logo.png"))
//                .setWidth(100)
//                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
//        document.add(logo);

        // Add report title
        Paragraph title = new Paragraph("Báo cáo nhiệm vụ")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(title);

        // Add user information
        Paragraph userInfo = new Paragraph("Thành viên: " + user.getFullname())
                .setFont(italicFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(userInfo);

        // Create table for tasks
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 4, 2, 2, 2, 2, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Add table headers
        String[] headers = {"Tiêu đề", "Mô tả", "Ngày bắt đầu", "Ngày hết hạn", "Trạng thái", "Lương nhiệm vụ", "Lương thực hưởng"};
        for (String header : headers) {
            table.addHeaderCell(
                    new Cell().add(new Paragraph(header).setFont(boldFont))
                            .setBackgroundColor(new DeviceRgb(242, 242, 242))
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setPadding(10)
                            .setFontSize(12)
            );
        }

        // Add data to the table
        for (Issue issue : issues) {
            addCell(table, issue.getTitle(), font);
            addCell(table, issue.getDescription(), font);
            addCell(table, issue.getStartDate().toString(), font);
            addCell(table, issue.getDueDate().toString(), font);
            addCell(table, issue.getStatus() + " " + issue.getFinish() + "%", font);
            addCell(table, String.valueOf(issue.getPrice()), font);

            String salaryInfo = "Chưa có thông tin lương";
            for (UserIssueSalary salary : issue.getSalaries()) {
                if (salary.getUser().getId() == user.getId()) {
                    salaryInfo = salary.getSalary() + " " + salary.getCurrency();
                    break;
                }
            }
            addCell(table, salaryInfo, font);
        }

        document.add(table);

        // Add summary
        Paragraph summary = new Paragraph("Tổng số nhiệm vụ: " + issues.size())
                .setFont(boldFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginTop(20);
        document.add(summary);

        // Add footer with page numbers
        int pageNumbers = pdfDocument.getNumberOfPages();
        for (int i = 1; i <= pageNumbers; i++) {
            document.showTextAligned(new Paragraph(String.format("Trang %s của %s", i, pageNumbers))
                            .setFont(font)
                            .setFontSize(10)
                            .setFontColor(ColorConstants.GRAY),
                    559, 20, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
        }

        document.close();

        byte[] pdfBytes = outputStream.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Helper method to add styled cells to the table
    private void addCell(Table table, String content, PdfFont font) {
        table.addCell(
                new Cell().add(new Paragraph(content).setFont(font))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setPadding(8)
                        .setFontSize(10)
        );
    }

}
