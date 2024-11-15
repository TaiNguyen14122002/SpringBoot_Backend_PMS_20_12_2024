package com.TaiNguyen.ProjectManagementSystems.Modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String fullname;
    private String email;
    private String address;
    private LocalDate createdDate = LocalDate.now();
    private String phone;
    private String company;
    private String programerposition;
    private List<String> selectedSkills = new ArrayList<>();
    private String introduce;
    private String avatar;

    //An Mat Khau
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
    private List<Issue>assignedIssues = new ArrayList<>();

    private int projectSize;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkingType> workTypes = new ArrayList<>();




}
