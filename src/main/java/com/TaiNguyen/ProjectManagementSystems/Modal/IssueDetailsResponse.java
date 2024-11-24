package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

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
}
