package com.TaiNguyen.ProjectManagementSystems.Utill;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CorrectPassword {
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean verityPassword(String password, String correctPassword) {
        return passwordEncoder.matches(password, correctPassword);
    }

    // Hàm kiểm tra email hợp lệ
    public boolean isValidEmail(String email) {
        // Quy tắc: chỉ cho phép ký tự chữ cái, số, dấu chấm, dấu gạch dưới và dấu gạch ngang
        String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Hàm kiểm tra mật khẩu hợp lệ
    public boolean isValidPassword(String password) {
        // Quy tắc: ít nhất 8 ký tự, có ít nhất 1 chữ thường, 1 chữ hoa, 1 số, và 1 ký tự đặc biệt
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }
}
