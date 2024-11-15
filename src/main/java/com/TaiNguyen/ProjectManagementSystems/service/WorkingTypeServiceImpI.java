package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.WorkingType;
import com.TaiNguyen.ProjectManagementSystems.repository.ProjectRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.WorkingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkingTypeServiceImpI implements WorkingTypeService{

    @Autowired
    private WorkingTypeRepository workingTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;


    @Override
    public WorkingType createWorkingType(Long userId, Long projectId, String workingType) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        WorkingType addworkingType = new WorkingType();
        addworkingType.setUser(user);
        addworkingType.setProject(project);
        addworkingType.setWorkType(workingType);

        return workingTypeRepository.save(addworkingType);
    }

    @Override
    public WorkingType updateWorkType(Long userId, Long projectId, String workingType) {
        WorkingType existingWorkType = workingTypeRepository.findByUserIdAndProjectId(userId, projectId);

        if(existingWorkType != null) {
            existingWorkType.setWorkType(workingType);
            return workingTypeRepository.save(existingWorkType);
        } else {
            throw new RuntimeException("Working Type Not Found");
        }
    }


}
