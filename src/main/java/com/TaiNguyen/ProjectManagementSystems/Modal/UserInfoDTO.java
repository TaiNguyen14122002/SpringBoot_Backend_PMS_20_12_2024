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
    private String email;
    private String phone;
    private String avatar;
    private Long issues; // Đổi từ int sang Long
    private String fileName;
}
