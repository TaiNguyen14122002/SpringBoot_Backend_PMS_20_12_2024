package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TaskCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String label;

    @ManyToOne
    private Project project;
}
