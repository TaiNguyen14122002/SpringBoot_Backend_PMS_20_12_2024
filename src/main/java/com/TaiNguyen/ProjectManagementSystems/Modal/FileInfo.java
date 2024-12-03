package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = true)
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private LocalDate uploadDate = LocalDate.now();
}
