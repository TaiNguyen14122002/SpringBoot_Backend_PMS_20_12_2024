package com.TaiNguyen.ProjectManagementSystems.repository;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;
import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.WorkingType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkingTypeRepository extends JpaRepository<WorkingType, Long> {

    WorkingType findByUserIdAndProjectId(Long userId, Long projectId);

}
