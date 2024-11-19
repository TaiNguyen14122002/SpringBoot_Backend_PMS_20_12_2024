package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.service.UserIssueSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update")
    public ResponseEntity<UserIssueSalary> updateSalary(@RequestParam Long userId, @RequestParam Long issueId, @RequestParam BigDecimal salary) throws Exception {
        UserIssueSalary updatedSalary = userIssueSalaryService.updateSalary(userId, issueId, salary, "VND");
        return ResponseEntity.ok(updatedSalary);
    }
}
