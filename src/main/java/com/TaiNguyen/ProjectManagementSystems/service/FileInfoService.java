package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileInfoService {
    public FileInfo AaddFile(String fileName, Long projectId, Long issueId, Long userId) throws IOException;

    public void deletedFile(long id);

    public List<FileInfo> getIfleByProjectId(Long projectId);

    public List<FileInfo> getFileByIssueId(Long issueId);

    public List<FileInfo> getFileByUserId(Long userId);

    public FileInfo addOrUpdateFile(String fileName, Long userId);
}
