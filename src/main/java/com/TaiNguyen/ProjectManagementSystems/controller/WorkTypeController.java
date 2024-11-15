package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.WorkingType;
import com.TaiNguyen.ProjectManagementSystems.service.WorkingTypeService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/worktype")
public class WorkTypeController {

    @Autowired
    private WorkingTypeService workingTypeService;

    @PutMapping("/update")
    public WorkingType updateWorkingType(@RequestParam Long userId, @RequestParam Long projectId, @RequestParam String workingType) {
        return workingTypeService.updateWorkType(userId, projectId, workingType);
    }

    @PostMapping("/addWorkType")
    public ResponseEntity<WorkingType> addWorkingType(@RequestParam Long userId, @RequestParam Long projectId, @RequestParam String workingType) {
        if (workingType == null || workingType.isEmpty()) {
            workingType = "Trực tuyến";
        }
        WorkingType createWorkType = workingTypeService.createWorkingType(userId, projectId, workingType);
        return new ResponseEntity<>(createWorkType, HttpStatus.OK);
    }
}
