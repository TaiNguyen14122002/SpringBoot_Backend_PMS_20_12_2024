package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import jdk.jshell.spi.ExecutionControl;

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
}
