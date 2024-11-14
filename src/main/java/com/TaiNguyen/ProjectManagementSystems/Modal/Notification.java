package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    private String content;


    private LocalDateTime timestamp = LocalDateTime.now();


    private boolean isRead = false;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Issue issue;
}
