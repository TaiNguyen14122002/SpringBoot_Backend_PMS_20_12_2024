package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail( String email);


    // Truy vấn lấy thông tin các thành viên trong một dự án cụ thể
    @Query("SELECT new com.TaiNguyen.ProjectManagementSystems.Modal.UserInfoDTO(" +
            "u.id, u.fullname, u.programerposition, COALESCE(wt.workType, 'Not Assigned'), " +
            "u.email, u.phone, u.avatar, COUNT(DISTINCT i.id), COALESCE(fi.fileName, 'No File')) " +
            "FROM User u " +
            "LEFT JOIN WorkingType wt ON wt.user.id = u.id AND wt.project.id = :projectId " +
            "LEFT JOIN Issue i ON i.assignee.id = u.id AND i.project.id = :projectId " +
            "LEFT JOIN FileInfo fi ON fi.user.id = u.id " +
            "WHERE u.id IN (SELECT member.id FROM Project p JOIN p.team member WHERE p.id = :projectId) " +
            "GROUP BY u.id, u.fullname, u.programerposition, wt.workType, u.email, u.phone, u.avatar, fi.fileName")
    List<UserInfoDTO> findAllUsersByProjectId(Long projectId);

    boolean existsByEmail(String email);



}
