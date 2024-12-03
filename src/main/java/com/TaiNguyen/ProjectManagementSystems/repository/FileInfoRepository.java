package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    List<FileInfo> findByProjectId(Long projectId);

    List<FileInfo> findByIssueId(Long issueId);

    List<FileInfo> findByUserId(Long userId);

    Optional<FileInfo> findByUser_Id(Long userId);
}
