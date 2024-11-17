package com.TaiNguyen.ProjectManagementSystems.Modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
public class UserIssueSalary {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    private BigDecimal salary;

    private String currency;

    private boolean isPaid;
}
