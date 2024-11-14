package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = true)
    private Issue issueId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true)
    private Project projectId;

    private String fileName;
    private LocalDate uploadDate = LocalDate.now();

}
