package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IssueSalaryDTO {
    private Issue issue; // Nhiệm vụ
    private List<UserIssueSalary> salaries; // Lương cho từng n
}
