package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;

@Service
public class IssueImportExcelService {

    private final IssueRepository issueRepository;

    public IssueImportExcelService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public void importIssuesFromExcel(MultipartFile file) throws IOException {
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if(rowIterator.hasNext()) {
                rowIterator.next();
            }
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
//                Issue issue = mapRowToIssue(row);
//                issueRepository.save(issue);
            }
        }
    }

}
