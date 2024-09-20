package com.TaiNguyen.ProjectManagementSystems.Utill;

import com.TaiNguyen.ProjectManagementSystems.Modal.User;
import com.TaiNguyen.ProjectManagementSystems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailUtill emailUtill;

    private ConcurrentHashMap<String,String> otpStore = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Không tìm thấy tài khoản");
        }

        String otp = String.format("%06d", new Random().nextInt(9999999));

        otpStore.put(email,otp);

        String subject = "Mã OTP của bạn";
        String message = "Mã OTP của bạn là: " + otp;

        emailUtill.sendEmail(email,subject,message);
    }

    public boolean verifyOtp(String email, String otp) {
        return otpStore.get(email).equals(otp);
    }
}
