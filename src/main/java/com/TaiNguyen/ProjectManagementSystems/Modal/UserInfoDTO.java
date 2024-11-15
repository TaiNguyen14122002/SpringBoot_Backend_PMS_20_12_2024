package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long userId;
    private String fullName;
    private String programerPosition;
    private String workType;
    private Long totalAssignedIssues; // Đổi từ int sang Long
}
