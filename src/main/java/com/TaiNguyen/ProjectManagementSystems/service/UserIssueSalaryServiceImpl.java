package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.repository.UserIssueSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserIssueSalaryServiceImpl implements UserIssueSalaryService {

    @Autowired
    private UserIssueSalaryRepository userIssueSalaryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;

    @Override
    public UserIssueSalary addSalary(Long userId, Long issueId, BigDecimal salary, String currency) throws Exception {
        User user = userService.findUserById(userId);

        Issue issue = issueService.getIssueById(issueId);

        UserIssueSalary userIssueSalary = new UserIssueSalary();
        userIssueSalary.setUser(user);
        userIssueSalary.setIssue(issue);
        userIssueSalary.setSalary(salary);
        userIssueSalary.setCurrency(currency);
        return userIssueSalaryRepository.save(userIssueSalary);
    }
}
