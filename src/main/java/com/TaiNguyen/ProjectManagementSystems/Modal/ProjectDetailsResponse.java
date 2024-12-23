package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ProjectDetailsResponse {
    private long id;
    private String name;
    private String description;
    private String category;
    private List<String> tags;
    private List<String> fileNames;
    private List<String> goals;
    private LocalDate createdDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal fundingAmount;
    private BigDecimal profitAmount;
    private List<TeamMemberResponse> teamMembers;
}
