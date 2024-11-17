package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.service.UserIssueSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/salaries")
public class UserIssueSalaryController {

    @Autowired
    private UserIssueSalaryService userIssueSalaryService;

    @PostMapping("/addSalaries")
    public ResponseEntity<UserIssueSalary> addSalary(@RequestParam Long userId, @RequestParam Long issueId, @RequestParam BigDecimal salary) throws Exception {
        UserIssueSalary userIssueSalary = userIssueSalaryService.addSalary(userId, issueId, salary, "VND");
        return ResponseEntity.ok(userIssueSalary);
    }
}
