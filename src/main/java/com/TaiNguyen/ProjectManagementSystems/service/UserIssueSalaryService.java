package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserIssueSalaryService {

    public UserIssueSalary addSalary(Long userId, Long issueId, BigDecimal salary, String currency) throws Exception;

    public UserIssueSalary updateSalary(Long userId, Long issueId, BigDecimal salary, String currency) throws Exception;


}
