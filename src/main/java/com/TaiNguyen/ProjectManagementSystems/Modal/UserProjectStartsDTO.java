package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProjectStartsDTO {
    private String userName;
    private String projectName;
    private int totalIssueAssigned;
    private BigDecimal totalSalaryPaid;
    private int totalCompletedIssues;
    private List<IssueDTO> assignedIssues;
}
