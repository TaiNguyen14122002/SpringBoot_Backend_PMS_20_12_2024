package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.*;

@Service
public class IssueServiceImpl implements IssueService{

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    @Override
    public Issue getIssueById(Long issueId) throws Exception {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if(issue.isPresent()){
            return issue.get();
        }
        throw new Exception("No issues found with issueid" + issueId);
    }

    @Override
    public List<Issue> getIssuesByUserId(User user) throws Exception {
        return issueRepository.findByAssignee(user);
    }


    @Override
    public List<Issue> getIssueByProjectId(Long projectId) throws Exception {
        return issueRepository.findByProjectId(projectId);
    }

    @Override
    public Issue createIssue(IssueRequest issueRequest, User user) throws Exception {
        Project project = projectService.getProjectById(issueRequest.getProjectId());

        Issue issue = new Issue();
        issue.setTitle(issueRequest.getTitle());
        issue.setDescription(issueRequest.getDescription());
        issue.setStatus(issueRequest.getStatus());
        issue.setProjectID(issueRequest.getProjectId());
        issue.setPriority(issueRequest.getPriority());
        issue.setDueDate(issueRequest.getDueDate());
        issue.setStartDate(issueRequest.getStartDate());

        issue.setProject(project);

        return issueRepository.save(issue);
    }

    @Override
    public void deleteIssue(Long issueId, Long userid) throws Exception {
        getIssueById(issueId);
        issueRepository.deleteById(issueId);
    }

    @Override
    public Issue addUserToIssue(Long issueId, Long userId) throws Exception {
        User user = userService.findUserById(userId);
        Issue issue = getIssueById(issueId);

         issue.setAssignee(user);
         return issueRepository.save(issue);

    }

    @Override
    public Issue updateIssue(Long issueId, String status) throws Exception {
        Issue issue = getIssueById(issueId);

        issue.setStatus(status);
        return issueRepository.save(issue);

    }

    @Override
    public boolean checkProjectOwner(Long projectId, Long userId) throws Exception {
        Project project = projectService.getProjectById(projectId);

        if (project != null && project.getOwner() != null && project.getOwner().getId() == userId) {
            return true;
        }

        return false;

    }

    @Override
    public Map<String, Long> getIssueCountByStatus(Long projectId) throws Exception {
        List<Issue> issues = issueRepository.findByProjectId(projectId);

        long inProgressCount = issues.stream()
                .filter(issue -> "in progress".equalsIgnoreCase(issue.getStatus()))
                .count();
        long notStartedCount = issues.stream()
                .filter(issue -> "not started".equalsIgnoreCase(issue.getStatus()))
                .count();
        long doneCount = issues.stream()
                .filter(issue -> "done".equalsIgnoreCase(issue.getStatus()))
                .count();

        Map<String, Long> issueCountByStatus = new HashMap<>();
        issueCountByStatus.put("inprogress", inProgressCount);
        issueCountByStatus.put("notstarted", notStartedCount);
        issueCountByStatus.put("done", doneCount);

        return issueCountByStatus;
    }

    @Override
    public List<Issue> allIssuesByAssigneeId(Long userId) throws Exception {
        return issueRepository.findAllIssueByAssigneeId(userId);

    }

    @Override
    public List<Issue> getIssueByProjectAndAssigneeId(Long projectId, Long userId) throws Exception {
        return issueRepository.findByProject_IdAndAssignee_Id(projectId, userId);
    }

    @Override
    public Map<String, Long> getUserIssueStatisticsByProject(Long userId, Long projectId) {
        Map<String, Long> issueStatistics = new HashMap<>();
        issueStatistics.put("total", issueRepository.countTotalIssuesByUserAndProject(userId, projectId));
        issueStatistics.put("pending", issueRepository.countTodoIssuesByUserAndProject(userId, projectId));
        issueStatistics.put("inProgress", issueRepository.countInProgressIssuesByUserAndProject(userId, projectId));
        issueStatistics.put("done", issueRepository.countDoneIssuesByUserAndProject(userId, projectId));
        return issueStatistics;

    }

    public List<Map<String, Object>> getIssueCountByPriorityForUserProjects(Long userId) {
        List<Object[]> results = issueRepository.countIssuesByPriorityForUserProjects(userId);
        List<Map<String, Object>> priorityCounts = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("priority", result[0]);
            data.put("count", result[1]);
            priorityCounts.add(data);
        }
        return priorityCounts;
    }

    @Override
    public Map<String, Long> getIssueCountByPriority(Long projectId) {
        List<Object[]> results = issueRepository.countIssuesByPriority(projectId);
        Map<String, Long> priorityCounts = new HashMap<>();
        for (Object[] result : results) {
            String priority = (String) result[0];
            Long count = (Long) result[1];
            priorityCounts.put(priority, count);
        }
        return priorityCounts;
    }

    @Override
    public Map<String, Long> getIssueCountByStatuss(Long projectId) {
        List<Object[]> results = issueRepository.countIssuesByStatus(projectId);
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status, count);
        }
        return statusCounts;
    }

    @Override
    public Map<String, Map<String, Long>> getIssueCountByStatusAndAssignee(Long projectId) {
        List<Object[]> results = issueRepository.countIssuesByStatusAndAssignee(projectId);
        Map<String, Map<String, Long>> statusCounts = new HashMap<>();

        for(Object[] result : results) {
            String assigneeName = result[0] != null ? (String) result[0] : "Chưa phân công";
            String status = (String) result[1];
            Long count = (Long) result[2];

            statusCounts.computeIfAbsent(assigneeName, k -> new HashMap<>())
                    .put(status, count);
        }
        return statusCounts;
    }

    @Override
    public double getIssueDoneRatioForProject(Long projectId) {
        long completedCount = issueRepository.countCompletedIssuesByProject(projectId);
        long inProgressOrNotStartedCount = issueRepository.countInProgressOrNotStartedIssuesByProject(projectId);

        long totalCount = completedCount + inProgressOrNotStartedCount;
        return ((int) completedCount * 100 / totalCount);
    }

    @Override
    public void uploadFileToIssue(Long IssueId, Issue file) throws Exception {
        Issue issue = getIssueById(IssueId);
        issue.getFileNames().addAll(file.getFileNames());
        issueRepository.save(issue);
    }
}
