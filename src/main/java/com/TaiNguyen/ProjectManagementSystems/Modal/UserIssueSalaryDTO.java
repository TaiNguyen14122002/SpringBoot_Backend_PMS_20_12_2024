package com.TaiNguyen.ProjectManagementSystems.Modal;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserIssueSalaryDTO {

    private long id;

    private User user;
    private BigDecimal salary;
    public String currency;
    private boolean isPaid;
}
