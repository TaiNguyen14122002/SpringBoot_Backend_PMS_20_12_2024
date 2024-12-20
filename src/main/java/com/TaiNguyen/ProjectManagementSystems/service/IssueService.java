package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.IssueDTO;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import jakarta.mail.MessagingException;
import jdk.jshell.spi.ExecutionControl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IssueService {
    Issue getIssueById(Long issueId) throws Exception;

    List<Issue> getIssuesByUserId(User user) throws Exception;

    List<Issue> getIssueByProjectId(Long projectId) throws Exception;

    Issue createIssue(IssueRequest issue, User user) throws Exception;

    void deleteIssue(Long issueId, Long userid) throws Exception;

    Issue addUserToIssue(Long issueId, Long userId) throws Exception;

    Issue updateIssue(Long issueId, String status) throws Exception;

    Issue updateFinishIssue( Long issueId, String finish) throws Exception;

    boolean checkProjectOwner(Long projectId, Long userId) throws Exception;

    Map<String, Long> getIssueCountByStatus(Long projectId) throws Exception;

    List<Issue> allIssuesByAssigneeId(Long userId) throws Exception;

    List<Issue> getIssueByProjectAndAssigneeId(Long projectId, Long userId) throws Exception;

    Map<String, Long> getUserIssueStatisticsByProject(Long userId, Long projectId);

    public List<Map<String, Object>> getIssueCountByPriorityForUserProjects(Long userId);

    Map<String, Long> getIssueCountByPriority(Long projectId);

    Map<String, Long> getIssueCountByStatuss(Long projectId);

    Map<String, Map<String, Long>> getIssueCountByStatusAndAssignee(Long projectId);

    double getIssueDoneRatioForProject(Long projectId);

    void uploadFileToIssue(Long IssueId, Issue file) throws Exception;

    public List<Issue> getAllIssuesByOwnerId(Long ownerId) throws Exception;

    public List<IssueDTO> getIssuesByProject(long projectId) throws Exception;

    public List<IssueDTO> getIssuesByUser(long userId);

    public List<Object[]> getAllIssuesWithSalaryByUserId(long userId) throws Exception;

    public List<Issue> getExpiringIssues(User assignee) throws Exception;

    public List<Issue> getExpiredIssues(User assignee) throws Exception;

    public Issue updateDueDate(Long issueId, LocalDate dueDate);

    public Optional<Issue> findByIdAndProject(long issueId, Project project);

    void checkAndNotifyIssuesDueSoon() throws MessagingException;

    List<Issue> getIssuesDueSoon();


}
