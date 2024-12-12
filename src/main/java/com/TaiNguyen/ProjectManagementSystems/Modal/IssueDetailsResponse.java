package com.TaiNguyen.ProjectManagementSystems.Modal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class IssueDetailsResponse {
    private long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String tags;
    private String salary;
    private LocalDate dueDate;
    private LocalDate actualDate;
    private String finish;
}
