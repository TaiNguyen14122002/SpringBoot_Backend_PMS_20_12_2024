package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String fullname;
    private String email;
    private String phone;
    private String company;
    private String programerposition;
    private LocalDate createdDate;
    private String avatar;
}
