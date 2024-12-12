package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.FileInfo;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.service.FileInfoService;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/file-info")
public class FileInfoController {

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private UserService userService;

    @PostMapping("/addFile")
    public ResponseEntity<FileInfo> addFile(@RequestParam("fileUrl") String fileUrl,
                                            @RequestParam(value = "projectId", required = false) Long projectId,
                                            @RequestParam(value = "issueId", required = false) Long issueId,
                                            @RequestParam(value = "userId", required = false) Long userId) throws IOException {
        FileInfo fileInfo = fileInfoService.AaddFile(fileUrl,projectId, issueId, userId);
        return ResponseEntity.ok(fileInfo);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<FileInfo> deletedFile(@PathVariable long id){
        try {
            fileInfoService.deletedFile(id);
            return ResponseEntity.ok().build();

        }catch (Exception e){
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<FileInfo>> getFilesByProjectId(@PathVariable Long projectId){
        List<FileInfo> fileInfos = fileInfoService.getIfleByProjectId(projectId);
        if(fileInfos.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(fileInfos);
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<List<FileInfo>> getFilesByIssueId(@PathVariable Long issueId){
        List<FileInfo> fileInfos = fileInfoService.getFileByIssueId(issueId);
        if(fileInfos.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(fileInfos);
    }

    @GetMapping("/user")
    public ResponseEntity<List<FileInfo>> getFilesByUserId(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<FileInfo> fileInfos = fileInfoService.getFileByUserId(user.getId());
        if(fileInfos.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(fileInfos);
    }

    @PostMapping("/addOrUpdate")
    public FileInfo ADDOrUpdateFile(@RequestHeader("Authorization") String jwt, @RequestParam String fileName) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return fileInfoService.addOrUpdateFile(fileName, user.getId());
    }

    @GetMapping("/UserAssigner/{userId}")
    public ResponseEntity<List<FileInfo>> getFilesByUserAssignerId(@RequestHeader("Authorization") String jwt, @PathVariable long userId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<FileInfo> fileInfos = fileInfoService.getFileByUserId(userId);
        if(fileInfos.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(fileInfos);
    }
}
