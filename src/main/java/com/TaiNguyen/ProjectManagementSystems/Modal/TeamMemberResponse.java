package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TeamMemberResponse {
    private long id;
    private String fullname;
    private String email;
    private String phone;
    private String company;
    private String programerposition;
    private LocalDate createdDate;
    private String avatar;
    private List<IssueDetailsResponse> issues; // Danh sách nhiệm vụ của thành viên
    private BigDecimal totalSalaryIssue;
}
