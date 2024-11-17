package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;

import java.math.BigDecimal;

public interface UserIssueSalaryService {

    public UserIssueSalary addSalary(Long userId, Long issueId, BigDecimal salary, String currency) throws Exception;
}
