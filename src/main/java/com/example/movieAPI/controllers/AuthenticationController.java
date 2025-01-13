package com.example.movieAPI.controllers;

import com.example.movieAPI.dtos.request.LoginRequest;
import com.example.movieAPI.dtos.request.RefreshTokenRequest;
import com.example.movieAPI.dtos.request.RegisterRequest;
import com.example.movieAPI.dtos.response.ApiResponse;
import com.example.movieAPI.dtos.response.AuthResponse;
import com.example.movieAPI.entities.RefreshToken;
import com.example.movieAPI.entities.User;
import com.example.movieAPI.services.AuthenService;
import com.example.movieAPI.services.JwtService;
import com.example.movieAPI.services.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final AuthenService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthenticationController(AuthenService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(new ApiResponse("Register successfully" ,authService.register(registerRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(new ApiResponse("Login successfully", authService.login(loginRequest)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(new ApiResponse("Refresh token successfully", AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build()));
    }
}
