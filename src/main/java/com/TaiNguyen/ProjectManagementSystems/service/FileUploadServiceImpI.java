package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileUpload;
import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileUploadServiceImpI implements FileUploadService{

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectService projectService;



    @Override
    public FileUpload saveFielForIssue(Long issueId, String FileName) throws Exception {
        Issue issue = issueService.getIssueById(issueId);
        FileUpload fileUpload = new FileUpload();
        fileUpload.setIssueId(issue);
        fileUpload.setFileName(FileName);
        return fileUploadRepository.save(fileUpload);
    }

    @Override
    public FileUpload saveFileForProject(Long projectId, String FileName) throws Exception {
        Project project = projectService.getProjectById(projectId);
        FileUpload fileUpload = new FileUpload();
        fileUpload.setProjectId(project);
        fileUpload.setFileName(FileName);
        return fileUploadRepository.save(fileUpload);
    }
}
