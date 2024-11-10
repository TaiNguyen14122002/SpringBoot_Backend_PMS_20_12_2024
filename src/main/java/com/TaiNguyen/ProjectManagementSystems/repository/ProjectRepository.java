package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {


    List<Project> findByNameContainingAndTeamContains(String partialName, User user);

    List<Project> findByTeamContainingOrOwner(User user, User owner);

    // Truy vấn các dự án đã bị xóa (action = -1) của người dùng theo ownerId
    List<Project> findByOwnerIdAndAction(long ownerId, int action);




    long countByOwner(User owner);

    @Query("SELECT COUNT(p) FROM Project p JOIN p.team t WHERE t.id = :userId")
    long findParticipatedProjects(@Param("userId") Long userId);



    // Truy vấn các dự án mà người dùng là chủ sở hữu trong năm
    @Query("SELECT p FROM Project p WHERE p.owner.id = :userId AND FUNCTION('YEAR', p.createdDate) = :year")
    List<Project> findProjectsByOwnerInYear(@Param("userId") Long userId, @Param("year") int year);

    // Truy vấn các dự án mà người dùng tham gia trong năm
    @Query("SELECT p FROM Project p INNER JOIN p.team t WHERE t.id = :userId AND FUNCTION('YEAR', p.createdDate) = :year")
    List<Project> findProjectsByTeamMemberInYear(@Param("userId") Long userId, @Param("year") int year);

    // Lấy tất cả các dự án đã ghim
    List<Project> findByAction(int action);

    // Lấy tất cả các dự án chưa bị xóa (action != -1)
    List<Project> findByActionNot(int action);

    // Truy vấn danh sách các dự án mà người dùng tham gia với action = 1
    @Query("SELECT p FROM Project p JOIN p.team t WHERE t.id = :userId AND p.action = 1")
    List<Project> findProjectPinnedByUser(@Param("userId") Long userId);

    // Truy vấn các dự án mà người dùng tham gia và sắp hết hạn trong vòng 7 ngày
    @Query("SELECT p FROM Project p JOIN p.team u WHERE u = :user AND p.endDate BETWEEN :currentDate AND :nextWeek AND p.status = 'In_Progress'")
    List<Project> findExpiringProjectsByUser(User user, LocalDate currentDate, LocalDate nextWeek);

    // Truy vấn các dự án có endDate nhỏ hơn ngày hiện tại và action != -1 (không bị xóa logic)
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.action != -1")
    List<Project> findExpiredProjects(LocalDate currentDate);

}
