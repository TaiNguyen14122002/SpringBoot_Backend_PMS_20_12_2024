package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserIssueSalary;
import com.TaiNguyen.ProjectManagementSystems.repository.UserIssueSalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

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

    @Override
    public UserIssueSalary updateSalary(Long userId, Long issueId, BigDecimal salary, String currency) throws Exception {
        User user = userService.findUserById(userId);
        if(user == null){
            throw new Exception("User not found");
        }

        Issue issue = issueService.getIssueById(issueId);
        if(issue == null){
            throw new Exception("Issue not found");
        }

        Optional<UserIssueSalary> optionalSalary = userIssueSalaryRepository.findByUserAndIssue(user, issue);

        if(optionalSalary.isPresent()){
            UserIssueSalary userIssueSalary = optionalSalary.get();
            userIssueSalary.setSalary(salary);
            userIssueSalary.setCurrency(currency);

            return userIssueSalaryRepository.save(userIssueSalary);
        }else{
            throw new Exception("UserIssueSalary not found");
        }
    }
}
