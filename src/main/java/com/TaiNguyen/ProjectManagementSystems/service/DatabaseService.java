package com.TaiNguyen.ProjectManagementSystems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void disableSafeMode() {
        String sql = "SET SQL_SAFE_UPDATES = 0";
        jdbcTemplate.execute(sql);
    }

    // Phương thức bật lại safe mode
    public void enableSafeMode() {
        String sql = "SET SQL_SAFE_UPDATES = 1";
        jdbcTemplate.execute(sql);
    }
}
