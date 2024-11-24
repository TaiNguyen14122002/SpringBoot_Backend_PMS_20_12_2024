package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String fileName;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Issue issue;

    @ManyToOne
    private User user;
}
