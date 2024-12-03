package com.TaiNguyen.ProjectManagementSystems.service;

import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserInfoDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findUserProfileByJwt(String jwt)throws Exception;

    User findUserByEmail(String email)throws Exception;

    User findUserById(Long userId)throws Exception;

    User updateUsersProjectSize(User user, int number);

    String forgotPassword(String email) throws Exception;

    boolean resetPassword(String token, String newPassword) throws Exception;

    // Lưu token reset mật khẩu
    void saveResetPasswordToken(String email, String token) throws Exception;

    // Kiểm tra token có hợp lệ không và trả về email nếu token hợp lệ
    String validateResetPasswordToken(String token) throws Exception;

    public List<UserInfoDTO> getUserInfoByProjectId(Long projectId);

    public User updateUser ( long id, User user);

    public User findByEmail(String email);



}
