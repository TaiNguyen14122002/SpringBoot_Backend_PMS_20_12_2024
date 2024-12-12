package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    public List<Issue> findByProjectId(Long id);



    List<Issue> findByAssignee(User userId);

    @Query("SELECT i FROM Issue i WHERE i.assignee.id = :userId")
    List<Issue> findAllIssueByAssigneeId(@Param("userId") Long userId);

    // Sửa lại phương thức để tìm theo projectId và assigneeId
    List<Issue> findByProject_IdAndAssignee_Id(Long projectId, Long assigneeId);



    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignee.id = :userId AND i.project.id = :projectId")
    Long countTotalIssuesByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignee.id = :userId AND i.project.id = :projectId AND i.status = 'pending'")
    Long countTodoIssuesByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignee.id = :userId AND i.project.id = :projectId AND i.status = 'in_progress'")
    Long countInProgressIssuesByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignee.id = :userId AND i.project.id = :projectId AND i.status = 'done'")
    Long countDoneIssuesByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT i.priority, COUNT(i) FROM Issue i " +
            "WHERE i.assignee.id = :userId " + // Chỉ lấy nhiệm vụ đã được phân công cho người dùng
            "GROUP BY i.priority")
    List<Object[]> countIssuesByPriorityForUserProjects(@Param("userId") Long userId);

    @Query("SELECT i.priority, COUNT(i) FROM Issue i WHERE i.project.id = :projectId GROUP BY i.priority")
    List<Object[]> countIssuesByPriority(Long projectId);

    @Query("SELECT " +
            "CASE WHEN i.assignee IS NULL THEN 'notyetassigned' ELSE i.status END, " +
            "COUNT(i) " +
            "FROM Issue i " +
            "WHERE i.project.id = :projectId " +
            "GROUP BY CASE WHEN i.assignee IS NULL THEN 'notyetassigned' ELSE i.status END")
    List<Object[]> countIssuesByStatus(Long projectId);

    @Query("SELECT COALESCE(i.assignee.fullname, 'Unassigned') AS assignee, " +
            "i.status, COUNT(i) AS taskCount " +
            "FROM Issue i " +
            "WHERE i.project.id = :projectId " +
            "GROUP BY i.assignee.fullname, i.status")

    List<Object[]> countIssuesByStatusAndAssignee(Long projectId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.status = 'Hoàn thành' AND i.project.id = :projectId")
    long countCompletedIssuesByProject(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE (i.status = 'Chưa làm' OR i.status = 'Đang làm') AND i.project.id = :projectId")
    long countInProgressOrNotStartedIssuesByProject(@Param("projectId") Long projectId);

    // Truy vấn để lấy tất cả Issue của các Project mà user là owner
    @Query("SELECT i FROM Issue i WHERE i.project.owner.id = :ownerId")
    List<Issue> findAllIssuesByOwnerId(@Param("ownerId") Long ownerId);

    int countByAssignee(User assignee);

    @Query("SELECT i, u.salary, u.isPaid FROM Issue i " +
            "JOIN i.salaries u " +
            "WHERE u.user.id = :userId")
    List<Object[]> findAllIssuesWithSalaryByUserId(@Param("userId") Long userId);

    List<Issue> findByProjectIdAndAssigneeId(long projectId, long assigneeId);

    List<Issue> findByAssigneeAndDueDateBetweenAndStatusNot(User assignee, LocalDate startDate, LocalDate endDate, String status);

    List<Issue> findByAssigneeAndDueDateBeforeAndStatusNot(User assignee, LocalDate endDate, String status);

    List<Issue> findByAssigneeId(long assigneeId);

    Optional<Issue> findByIdAndProject(long issueId, Project project);
}
