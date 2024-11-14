package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.Project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface ExcelExportService {
    public ByteArrayOutputStream exportIssuesToExcel(List<Project> projects) throws IOException;
}
