package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class AssigneeResponse {
    private Long id;
    private String fullname;
    private String email;
    private String address;
    private LocalDate createdDate;
    private String phone;
    private String company;
    private String programerposition;
    private List<String> selectedSkills;
    private String introduce;
    private String avatar;
    private int projectSize;
}
