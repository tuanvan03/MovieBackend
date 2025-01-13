package com.example.movieAPI.controllers;

import com.example.movieAPI.dtos.MailBody;
import com.example.movieAPI.dtos.request.ChangePassword;
import com.example.movieAPI.dtos.response.ApiResponse;
import com.example.movieAPI.entities.ForgotPassword;
import com.example.movieAPI.entities.User;
import com.example.movieAPI.repositories.ForgotPasswordRepository;
import com.example.movieAPI.repositories.UserRepository;
import com.example.movieAPI.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgot-password")
@CrossOrigin(origins = "*")
public class ForgotPasswordController {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder, PasswordEncoder passwordEncoder1) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder1;
    }


    // send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<ApiResponse> verifyEmail(@PathVariable String email) {
        // Check user exits
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!" + email));
        // generate opt code
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request : " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 20 * 100000))
                .user(user)
                .build();

        // send otp
        emailService.sendSimpleMessage(mailBody);

        // save otp
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok(new ApiResponse("Email sent for verification!", null));
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<ApiResponse> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        // take user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

        // take forgot password
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        // Check time expiration
        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return ResponseEntity.ok(new ApiResponse("OTP has expired!", HttpStatus.EXPECTATION_FAILED));
        }

        return ResponseEntity.ok(new ApiResponse("OTP verified!", HttpStatus.ACCEPTED));
    }


    @PostMapping("/changePassword/{email}")
    public ResponseEntity<ApiResponse> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {
        // check password
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return ResponseEntity.ok(new ApiResponse("Please enter the password again!", HttpStatus.EXPECTATION_FAILED));
        }

        // encrypt new password
        String encodedPassword = passwordEncoder.encode(changePassword.password());

        // update
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok(new ApiResponse("Password has been changed!", null));
    }
//
    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
