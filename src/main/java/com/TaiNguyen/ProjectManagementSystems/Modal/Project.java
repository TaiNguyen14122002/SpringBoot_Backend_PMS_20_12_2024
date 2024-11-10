package com.TaiNguyen.ProjectManagementSystems.Modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String description;
    private String category;


    private List<String> tags = new ArrayList<>();

    @ElementCollection
    private List<String> fileNames = new ArrayList<>();

    @ElementCollection
    private List<String> goals = new ArrayList<>();



    @JsonIgnore
    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Chat chat;

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

    @ManyToMany
    private List<User> team = new ArrayList<>();

    private LocalDate createdDate = LocalDate.now();

    private LocalDate endDate; // Thêm ngày kết thúc

    private int action = 0;

    private String status = "In_Progress";

}
