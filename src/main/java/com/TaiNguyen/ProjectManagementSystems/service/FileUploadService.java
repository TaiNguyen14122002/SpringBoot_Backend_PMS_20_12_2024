package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileUpload;
import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;

public interface FileUploadService {



    public FileUpload saveFielForIssue(Long issueId, String FileName) throws Exception;

    public FileUpload saveFileForProject(Long projectId, String FileName) throws Exception;
}
