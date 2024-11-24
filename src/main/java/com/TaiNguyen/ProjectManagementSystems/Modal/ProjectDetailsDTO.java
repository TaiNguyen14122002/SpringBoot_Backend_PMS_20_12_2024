package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailsDTO {

    private Long id;
    private String projectName;
    private List<String> teamMembers;
    private List<String> issues;
    private BigDecimal fundingAmount;
    private BigDecimal profitAmount;
    private String projectStatus;
}
