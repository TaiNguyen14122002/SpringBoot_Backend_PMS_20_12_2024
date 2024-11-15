package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.WorkingType;

public interface WorkingTypeService {

    public WorkingType createWorkingType(Long userId, Long projectId, String workingType);

    public WorkingType updateWorkType(Long userId, Long projectId, String workingType);
}
