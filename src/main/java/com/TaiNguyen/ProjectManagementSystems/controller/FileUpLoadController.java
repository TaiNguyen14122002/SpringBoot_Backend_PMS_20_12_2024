package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileUpload;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.service.FileUploadService;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fileuploads")
public class FileUpLoadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload/issue/{issueId}")
    public ResponseEntity<FileUpload> uploadFileForIssue(@RequestHeader("Authorization") String jwt, @PathVariable Long issueId, @RequestBody FileUpload fileName) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if(user != null){
            FileUpload fileUpload = fileUploadService.saveFielForIssue(issueId, fileName.getFileName());
            return ResponseEntity.ok(fileUpload);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/upload/project/{projectId}")
    public ResponseEntity<FileUpload> uploadFileForProject(@RequestHeader("Authorization") String jwt, @PathVariable Long projectId, @RequestBody FileUpload fileName) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        if(user != null){
            FileUpload fileUpload = fileUploadService.saveFileForProject(projectId, fileName.getFileName());
            return ResponseEntity.ok(fileUpload);
        }
        return ResponseEntity.notFound().build();
    }
}
