package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class IssueResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long projectID;
    private String priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate actualDate;
    private String price;
    private String finish;
    private List<String> tags;
    private AssigneeResponse assignee;
    private boolean isOwner;
    private String salary;
    private List<String> fileNames;
}
