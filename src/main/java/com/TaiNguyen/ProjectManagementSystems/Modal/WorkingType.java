package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

public class WorkingType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String workType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
