package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileInfo;
import com.TaiNguyen.ProjectManagementSystems.Modal.Issue;
import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.repository.FileInfoRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileInfoServiceImpl implements FileInfoService{

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public FileInfo AaddFile(String fileName, Long projectId, Long issueId, Long userId) throws IOException {
        Optional<Project> projectOpt = (projectId != null) ? projectRepository.findById(projectId) : Optional.empty();
        Optional<Issue> issueOpt = (issueId != null) ? issueRepository.findById(issueId) : Optional.empty();
        Optional<User> userOpt = (userId != null) ? userRepository.findById(userId) : Optional.empty();

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        projectOpt.ifPresent(fileInfo::setProject); // Chỉ thiết lập nếu tồn tại
        issueOpt.ifPresent(fileInfo::setIssue);     // Chỉ thiết lập nếu tồn tại
        userOpt.ifPresent(fileInfo::setUser);       // Chỉ thiết lập nếu tồn tại
        fileInfo.setUploadDate(LocalDate.now());

        return fileInfoRepository.save(fileInfo);
    }

    @Override
    public void deletedFile(long id) {
        if(fileInfoRepository.existsById(id)) {
            fileInfoRepository.deleteById(id);
        }else {
            throw  new RuntimeException("File not found");
        }
    }

    @Override
    public List<FileInfo> getIfleByProjectId(Long projectId) {
        return fileInfoRepository.findByProjectId(projectId);
    }

    @Override
    public List<FileInfo> getFileByIssueId(Long issueId) {
        return fileInfoRepository.findByIssueId(issueId);
    }

    @Override
    public List<FileInfo> getFileByUserId(Long userId) {
        return fileInfoRepository.findByUserId(userId);
    }

    @Override
    public FileInfo addOrUpdateFile(String fileName, Long userId) {
        User user = userRepository.findById(userId).get();

        FileInfo fileInfo = fileInfoRepository.findByUser_Id(userId)
                .orElse(new FileInfo());

        if (fileInfo.getId() == 0) {
            fileInfo.setUser(user);
        }

        fileInfo.setFileName(fileName);
        fileInfo.setUploadDate(LocalDate.now());

        return fileInfoRepository.save(fileInfo);
    }

    @Override
    public List<Map<String,String>> getFileNamesByProjectOwner(Project project) {
        if(project.getName() == null){
            return List.of();
        }
        return fileInfoRepository.findByUser(project.getOwner())
                .stream()
                .map(fileInfo -> Map.of("Filename", fileInfo.getFileName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileInfo> getFilesByUser(User user) {
        return fileInfoRepository.findByUser(user);
    }


}
