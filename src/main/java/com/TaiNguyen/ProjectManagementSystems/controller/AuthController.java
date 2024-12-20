package com.TaiNguyen.ProjectManagementSystems.controller;

import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.Utill.CorrectPassword;
import com.TaiNguyen.ProjectManagementSystems.Utill.EmailUtill;
import com.TaiNguyen.ProjectManagementSystems.config.JwtProvider;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import com.TaiNguyen.ProjectManagementSystems.request.LoginRequest;
import com.TaiNguyen.ProjectManagementSystems.response.AuthResponse;
import com.TaiNguyen.ProjectManagementSystems.response.ErrorResponse;
import com.TaiNguyen.ProjectManagementSystems.response.SuccessResponse;
import com.TaiNguyen.ProjectManagementSystems.service.CustomeUserDetailsImpl;
import com.TaiNguyen.ProjectManagementSystems.service.SubscriptionService;
import com.TaiNguyen.ProjectManagementSystems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomeUserDetailsImpl customerUserDatails;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailUtill emailUtill;

    @Autowired
    private CorrectPassword correctPassword;


    @GetMapping("/list")
    public List<User> getAll(){
        return userRepository.findAll();
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customerUserDatails.loadUserByUsername(email);
        if(userDetails == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Mật khẩu bị sai");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }





    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if(!correctPassword.isValidEmail(user.getEmail())) {
            ErrorResponse errorResponse = new ErrorResponse("Email không hợp lệ. Vui lòng nhập đúng định dạng email.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        User isUser = userRepository.findByEmail(user.getEmail());


        if(isUser != null) {
            ErrorResponse errorResponse = new ErrorResponse("Tài khoản đã tồn tại với email: " + user.getEmail());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        if(!correctPassword.isValidPassword(user.getPassword())) {
            ErrorResponse error = new ErrorResponse("Mật khẩu không hợp lệ. Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        User CreatedUser = new User();
        CreatedUser.setEmail(user.getEmail());
        CreatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        CreatedUser.setFullname(user.getFullname());
        CreatedUser.setAddress(user.getAddress());
        CreatedUser.setCompany(user.getCompany());
        CreatedUser.setPhone(user.getPhone());
        CreatedUser.setProgramerposition(user.getProgramerposition());
        CreatedUser.setAvatar(user.getAvatar());
        CreatedUser.setSelectedSkills(user.getSelectedSkills());
        CreatedUser.setIntroduce(user.getIntroduce());

        User savedUser = userRepository.save(CreatedUser);

        SuccessResponse res = new SuccessResponse();
        res.setMessage("Đăng ký tài khoản thành công");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequest loginUserRequest){
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        User user = userRepository.findByEmail(email);

        if(user == null) {
            ErrorResponse error = new ErrorResponse("Tài khoản không tồn tại");
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        if(!CorrectPassword.verityPassword(password, user.getPassword())) {
            ErrorResponse error = new ErrorResponse("Mật khẩu không chính xác");
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse res = new AuthResponse();
        res.setMessage("Đăng nhập thành công");
        res.setFullName(user.getFullname());
        res.setEmail(user.getEmail());
        res.setToken(token);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            // Kiểm tra xem email có tồn tại trong hệ thống không
            if (!userService.isEmailExist(email)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Email không tồn tại trong hệ thống");
            }

            // Tạo mật khẩu mới
            String newPassword = "Tainguyen@0123";

            // Lấy thông tin người dùng từ email
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy người dùng với email này");
            }

            // Cập nhật mật khẩu mới đã mã hóa
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Kiểm tra nếu mật khẩu đã được thay đổi thành công
            if (user.getPassword() != null) {
                // Tạo token reset mật khẩu
                String token = UUID.randomUUID().toString();
                userService.saveResetPasswordToken(email, token);

                // Tạo liên kết reset mật khẩu
                String resetLink = "http://localhost:5173/";

                // Nội dung email
                String subject = "Yêu cầu đặt lại mật khẩu";
                String emailContent = "<!DOCTYPE html>"
                        + "<html lang=\"vi\">"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<title>Đặt lại mật khẩu</title>"
                        + "</head>"
                        + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4;\">"
                        + "<div style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                        + "<div style=\"background-color: #60A5FA; color: white; text-align: center; padding: 20px; border-radius: 8px 8px 0 0;\">"
                        + "<img src=\"https://firebasestorage.googleapis.com/v0/b/pms-fe88f.appspot.com/o/files%2FBlack%20and%20White%20Auto%20Repair%20Logo%20(1).png?alt=media&token=93954b37-4a53-43c2-a04e-7f49737cd55a\" alt=\"Logo công ty\" style=\"max-width: 150px; height: auto;\">"
                        + "<h1 style=\"margin: 10px 0;\">Đặt lại mật khẩu của bạn</h1>"
                        + "</div>"
                        + "<div style=\"padding: 20px; text-align: center;\">"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Dưới đây là thông tin tài khoản của bạn:</p>"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Email: <strong>" + email + "</strong></p>"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Mật khẩu: <strong>" + newPassword + "</strong></p>"
                        + "<p style=\"font-size: 16px; margin-top: 20px;\">Vui lòng thay đổi mật khẩu sau khi đăng nhập lần đầu.</p>"
                        + "</div>"
                        + "<div style=\"padding: 20px; text-align: center;\">"
                        + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>"
                        + "<a href=\"" + resetLink + "\" style=\"display: inline-block; background-color: #60A5FA; color: white; padding: 12px 24px; text-align: center; text-decoration: none; font-size: 18px; border-radius: 4px; transition: background-color 0.3s;\">Đến trang đăng nhập</a>"
                        + "<p style=\"font-size: 14px; color: #666; margin-top: 20px;\">Nếu nút không hoạt động, hãy sao chép và dán liên kết sau vào trình duyệt của bạn:</p>"
                        + "<p style=\"font-size: 14px; color: #60A5FA;\">" + resetLink + "</p>"
//                        + "<p style=\"font-size: 14px; color: #666; margin-top: 20px;\">Token sẽ tự động thay đổi trong 5 phút.</p>"
                        + "</div>"
                        + "<div style=\"background-color: #f8f8f8; text-align: center; padding: 10px; font-size: 12px; color: #666; border-radius: 0 0 8px 8px;\">"
                        + "<p>© 2023 Project Management Systems. Mọi quyền được bảo lưu.</p>"
                        + "<p>Nếu bạn cần hỗ trợ, vui lòng liên hệ <a href=\"2024801030129@student.tdmu.edu.vn\" style=\"color: #60A5FA;\">2024801030129@student.tdmu.edu.vn</a></p>"
                        + "</div>"
                        + "</div>"
                        + "</body>"
                        + "</html>";

                // Gửi email thông báo
                emailUtill.sendEmail(email, subject, emailContent);

                return new ResponseEntity<>("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn", HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Có lỗi xảy ra khi đổi mật khẩu.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String token) throws Exception {
        try{
            String email = userService.validateResetPasswordToken(token);
            if(email == null) {
                return new ResponseEntity<>("Token không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST);
            }
             String newPassword = "Tainguyen@023";

            if(!correctPassword.isValidPassword(newPassword)){
                ErrorResponse error = new ErrorResponse("Mật khẩu không hợp lệ. Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            //Đặt lại mật khẩu cho người dùng
            User user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return new ResponseEntity<>("Đặt lại mật khẩu thành công", HttpStatus.OK);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }





}
