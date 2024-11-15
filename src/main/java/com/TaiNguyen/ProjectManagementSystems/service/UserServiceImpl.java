package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserInfoDTO;
import com.TaiNguyen.ProjectManagementSystems.Utill.OTPService;
import com.TaiNguyen.ProjectManagementSystems.config.JwtProvider;
import com.TaiNguyen.ProjectManagementSystems.repository.IssueRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.repository.WorkingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Sử dụng một Map để lưu token tạm thời
    private Map<String, String> resetPasswordToken = new HashMap<>();
    private Map<String, LocalDateTime> tokenExpiryDate = new HashMap<>();
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private WorkingTypeRepository workingTypeRepository;

    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);

        return findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Không tìm thấy tài khoản");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new Exception("Không tìm thấy tài khoản");
        }
        return optionalUser.get();
    }

    @Override
    public String forgotPassword(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Không tìm thấy người dùng");
        }
        otpService.generateAndSendOtp(email);
        return "Mã OTP đã được gửi đến email của bạn";
    }

    @Override
    public boolean resetPassword(String token, String newPassword) throws Exception {

        //xác thực token
        String email = validateResetPasswordToken(token);
        if(email == null){
            throw new Exception("Token không hợp lệ hoặc đã hết hạn");
        }

        //Cập nhập mật khẩu mới
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Tài khoản không tồn tại");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        //Sau khi đặt lại thành công thì xoá token
        resetPasswordToken.remove(token);
        tokenExpiryDate.remove(token);
        return true;
    }

    @Override
    public void saveResetPasswordToken(String email, String token) throws Exception {
        // Lưu token cùng với email và thời gian hết hạn (ví dụ: 5 Phút)
        resetPasswordToken.put(token, email);
        tokenExpiryDate.put(token, LocalDateTime.now().plusMinutes(5));
    }

    @Override
    public String validateResetPasswordToken(String token) throws Exception {
        String email = resetPasswordToken.get(token);
        LocalDateTime expiryDate = tokenExpiryDate.get(token);
        if(email == null || expiryDate == null || expiryDate.isBefore(LocalDateTime.now())){
            return null; // Token không hợp lệ hoặc đã hết hạn
        }
        // Trả về email nếu token hợp lệ
        return email;
    }

    @Override
    public List<UserInfoDTO> getUserInfoByProjectId(Long projectId) {
        return userRepository.findAllUsersByProjectId(projectId);
    }


    @Override
    public User updateUsersProjectSize(User user, int number) {
        user.setProjectSize(user.getProjectSize()+number);
        if(user.getProjectSize() == -1){
            return userRepository.save(user);
        }
        return null;
    }


}
