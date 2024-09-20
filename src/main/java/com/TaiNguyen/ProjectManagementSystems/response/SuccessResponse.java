package com.TaiNguyen.ProjectManagementSystems.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    private String message;
    private String jwt;
}
