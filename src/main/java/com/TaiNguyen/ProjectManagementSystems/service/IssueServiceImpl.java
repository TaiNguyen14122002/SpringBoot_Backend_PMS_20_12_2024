package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.*;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserIssueSalaryRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.request.IssueRequest;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueServiceImpl implements IssueService{

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserIssueSalaryRepository userIssueSalaryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailUtill emailUtill;


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
        issue.setStatus("Chưa làm");
        issue.setProjectID(issueRequest.getProjectId());
        issue.setPriority(issueRequest.getPriority());
        issue.setDueDate(issueRequest.getDueDate());
        issue.setStartDate(issueRequest.getStartDate());
        issue.setPrice(issueRequest.getPrice());

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
        issue.setActualDate(LocalDate.now());
        return issueRepository.save(issue);

    }

    @Override
    public Issue updateFinishIssue(Long issueId, String finish) throws Exception {
        Issue issue = getIssueById(issueId);
        issue.setFinish(finish);
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
                .filter(issue -> "Chưa làm".equalsIgnoreCase(issue.getStatus()))
                .count();
        long notStartedCount = issues.stream()
                .filter(issue -> "Đang làm".equalsIgnoreCase(issue.getStatus()))
                .count();
        long doneCount = issues.stream()
                .filter(issue -> "Hoàn thành".equalsIgnoreCase(issue.getStatus()))
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

    @Override
    public List<Issue> getAllIssuesByOwnerId(Long ownerId) throws Exception {
        return issueRepository.findAllIssuesByOwnerId(ownerId);
    }

    @Override
    public List<IssueDTO> getIssuesByProject(long projectId) {
        // Lấy Project theo projectId
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Lấy tất cả các Issues của Project
        List<Issue> issues = issueRepository.findByProjectId(project.getId());

        // Chuyển đổi các Issue thành IssueDTO
        return issues.stream().map(issue -> {
            IssueDTO issueDTO = new IssueDTO();
            issueDTO.setId(issue.getId());
            issueDTO.setTitle(issue.getTitle());
            issueDTO.setDescription(issue.getDescription());
            issueDTO.setStatus(issue.getStatus());
            issueDTO.setProjectId(issue.getProjectID());
            issueDTO.setPriority(issue.getPriority());
            issueDTO.setStartDate(issue.getStartDate());
            issueDTO.setAssignes(issue.getAssignee());
            issueDTO.setDueDate(issue.getDueDate());
            issueDTO.setPrice(issue.getPrice());
            issueDTO.setFinish(issue.getFinish());
            issueDTO.setTags(issue.getTags());

            // Lấy danh sách UserIssueSalary cho mỗi Issue
            List<UserIssueSalary> salaries = userIssueSalaryRepository.findByIssue(issue);

            // Chuyển đổi danh sách UserIssueSalary thành UserIssueSalaryDTO
            List<UserIssueSalaryDTO> userIssueSalaryDTOs = salaries.stream()
                    .map(salary -> {
                        UserIssueSalaryDTO salaryDTO = new UserIssueSalaryDTO();
                        salaryDTO.setId(salary.getId());
                        salaryDTO.setUser(salary.getUser());  // Giả sử User có fullName
                        salaryDTO.setSalary(salary.getSalary());
                        salaryDTO.setCurrency(salary.getCurrency());
                        salaryDTO.setPaid(salary.isPaid());
                        return salaryDTO;
                    })
                    .collect(Collectors.toList());

            issueDTO.setUserIssueSalaries(userIssueSalaryDTOs);

            return issueDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> getIssuesByUser(long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("User ID is null");



        List<UserIssueSalary> userIssueSalaries= userIssueSalaryRepository.findByUser(user);

        List<Issue> issues = issueRepository.findByAssignee(user);

//        List<Issue> issues = issueRepository.findByProjectId(project.getId());

//        List<Issue> issues = userIssueSalaries.stream()
//                .map(UserIssueSalary::getIssue)
//                .distinct()
//                .collect(Collectors.toList());
//
        return issues.stream().map(issue -> {
            IssueDTO issueDTO = new IssueDTO();
            issueDTO.setId(issue.getId());
            issueDTO.setTitle(issue.getTitle());
            issueDTO.setDescription(issue.getDescription());
            issueDTO.setStatus(issue.getStatus());
            issueDTO.setProjectId(issue.getProjectID());
            issueDTO.setPriority(issue.getPriority());
            issueDTO.setStartDate(issue.getStartDate());
            issueDTO.setAssignes(issue.getAssignee());
            issueDTO.setDueDate(issue.getDueDate());
            issueDTO.setPrice(issue.getPrice());
            issueDTO.setFinish(issue.getFinish());
            issueDTO.setTags(issue.getTags());

            List<UserIssueSalaryDTO> salaries = userIssueSalaries.stream()
                    .filter(salary -> salary.getIssue().getId() == issue.getId())
                    .map(salary -> {
                        UserIssueSalaryDTO salaryDTO = new UserIssueSalaryDTO();
                        salaryDTO.setId(salary.getId());
                        salaryDTO.setUser(salary.getUser());
                        salaryDTO.setSalary(salary.getSalary());
                        salaryDTO.setCurrency(salary.getCurrency());
                        salaryDTO.setPaid(salary.isPaid());
                        return salaryDTO;
                    }).collect(Collectors.toList());

            issueDTO.setUserIssueSalaries(salaries);
            return issueDTO;
        }).collect(Collectors.toList());


    }

    @Override
    public List<Object[]> getAllIssuesWithSalaryByUserId(long userId) throws Exception {
        return issueRepository.findAllIssuesWithSalaryByUserId(userId);
    }

    @Override
    public List<Issue> getExpiringIssues(User assignee) throws Exception {
        LocalDate currentDate = LocalDate.now();
        System.out.println("Current Date: " + currentDate);
        LocalDate endDate = currentDate.plusDays(7);
        return issueRepository.findByAssigneeAndDueDateBetweenAndStatusNot(
                assignee, currentDate, endDate, "Hoàn thành");
    }

    @Override
    public List<Issue> getExpiredIssues(User assignee) throws Exception {
        LocalDate currentDate = LocalDate.now();
        return issueRepository.findByAssigneeAndDueDateBeforeAndStatusNot(assignee, currentDate, "Hoàn thành");
    }

    @Override
    public Issue updateDueDate(Long issueId, LocalDate dueDate) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setDueDate(dueDate);
        return issueRepository.save(issue);
    }

    @Override
    public Optional<Issue> findByIdAndProject(long issueId, Project project) {
        return issueRepository.findByIdAndProject(issueId, project);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?")  // 9h sáng
    @Scheduled(cron = "0 0 14 * * ?") // 2h chiều
    @Scheduled(cron = "0 0 17 * * ?") // 5h chiều
    @Scheduled(cron = "0 0 19 * * ?") // 7h tối
    public void checkAndNotifyIssuesDueSoon() throws MessagingException {
        List<Issue> issuesDueSoon = getIssuesDueSoon();
        for(Issue issue : issuesDueSoon) {
            sendNotification(issue);
        }
    }

    @Override
    public List<Issue> getIssuesDueSoon() {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(2);
        return issueRepository.findByDueDateBefore(threshold);
    }

    private void sendNotification(Issue issue) throws MessagingException {
        User assignee = issue.getAssignee();
        if(assignee != null){
            String subject = "Reminder: Task " + issue.getTitle() + "is about to be due!";
            String body = "Dear" + assignee.getFullname() + ",\n\nThe task '" + issue.getTitle() +
                    "' is about to be due on " + issue.getDueDate() + ". Please make sure to complete it on time.";
            emailUtill.sendEmail(assignee.getEmail(), subject, body);
        }

        Project project = issue.getProject();
        if(project != null){
            User projectOwner = project.getOwner();
            if(projectOwner != null){
                String subject = "Reminder: Task " + issue.getTitle() + " is about to be due!";
                String body = "Dear " + projectOwner.getFullname() + ",\n\nThe task '" + issue.getTitle() +
                        "' assigned to " + issue.getAssignee().getFullname() + " is about to be due on " + issue.getDueDate() + ".";
                emailUtill.sendEmail(projectOwner.getEmail(), subject, body);
            }
        }
    }



}
