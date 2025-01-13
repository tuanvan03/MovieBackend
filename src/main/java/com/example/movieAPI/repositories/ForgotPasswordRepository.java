package com.example.movieAPI.repositories;

import com.example.movieAPI.entities.ForgotPassword;
import com.example.movieAPI.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}
