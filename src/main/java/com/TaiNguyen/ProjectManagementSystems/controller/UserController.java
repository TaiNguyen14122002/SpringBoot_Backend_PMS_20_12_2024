package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Modal.UserInfoDTO;
import com.TaiNguyen.ProjectManagementSystems.Utill.CorrectPassword;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.response.ErrorResponse;
import com.TaiNguyen.ProjectManagementSystems.response.SuccessResponse;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrectPassword correctPassword;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String jwt, @RequestParam String newPassword) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        if(!correctPassword.isValidPassword(newPassword)){
            ErrorResponse error = new ErrorResponse("Mật khẩu không hợp lệ. Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        SuccessResponse succes = new SuccessResponse();
        succes.setMessage("Đổi mật khẩu thành công");
        succes.setJwt(jwt);
        return new ResponseEntity<>(succes, HttpStatus.OK);
    }

    @PutMapping("/changeInformation")
    public ResponseEntity<?> changeInfomation(@RequestHeader("Authorization") String jwt, @RequestBody User user) throws Exception {
        User userr = userService.findUserProfileByJwt(jwt);

        if(user.getPassword() != null && !correctPassword.isValidPassword(user.getPassword())){
            ErrorResponse error = new ErrorResponse("Mật khẩu không hợp lệ. Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if(user.getPassword() != null){
            userr.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if(user.getEmail() != null){
            userr.setEmail(user.getEmail());
        }
        if(user.getFullname() != null){
            userr.setFullname(user.getFullname());
        }
        userRepository.save(userr);

        SuccessResponse res = new SuccessResponse();
        res.setMessage("Cập nhập thông tin người dùng thành công");
        res.setJwt(jwt);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }

    // Endpoint lấy thông tin thành viên trong một dự án cụ thể
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<UserInfoDTO>> getAllUsersByProject(@PathVariable Long projectId) {
        List<UserInfoDTO> users = userService.getUserInfoByProjectId(projectId);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String jwt, @RequestBody User user) throws Exception {
        User userr = userService.findUserProfileByJwt(jwt);
        User updateuser = userService.updateUser(userr.getId(), user);
        return ResponseEntity.ok(updateuser);
    }



}

