package com.TaiNguyen.ProjectManagementSystems.Modal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class IssueUpdateDTO {
    private long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String price;
    private String finish;
    private String assigneeName;
    private String assigneeEmail;
    private BigDecimal salary;
    private String currency;

    @JsonDeserialize(using = YesNoDeserializer.class)
    private boolean isPaid;
}
