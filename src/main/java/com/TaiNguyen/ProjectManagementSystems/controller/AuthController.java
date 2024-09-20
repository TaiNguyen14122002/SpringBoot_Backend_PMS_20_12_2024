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
            String token = UUID.randomUUID().toString();
            userService.saveResetPasswordToken(email, token);

            String resetLink = "http://localhost:1000/auth/resetPassword?token=" + token;

            String subject = "Yêu cầu đặt lại mật khẩu";

            // Nội dung email với HTML và nút bấm
            String emailContent = "<h3>Đặt lại mật khẩu của bạn</h3>"
                    + "<p>Nhấn vào nút bên dưới để đặt lại mật khẩu của bạn:</p>"
                    + "<a href=\"" + resetLink + "\" style=\"display:inline-block;background-color:#4CAF50;color:white;padding:10px 20px;text-align:center;text-decoration:none;font-size:16px;\">Đặt lại mật khẩu</a>"
                    + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.</p>";

            emailUtill.sendEmail(email, subject, emailContent);
            return new ResponseEntity<>("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn", HttpStatus.OK);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PutMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) throws Exception {
        try{
            String email = userService.validateResetPasswordToken(token);
            if(email == null) {
                return new ResponseEntity<>("Token không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST);
            }

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
