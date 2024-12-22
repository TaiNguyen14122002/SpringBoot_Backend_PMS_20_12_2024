package com.TaiNguyen.ProjectManagementSystems.Modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    private Chat chat;

    @ManyToOne
    private User sender;




}
