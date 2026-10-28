package com.flowpay.controller;


import com.flowpay.dto.request.LoginRequest;
import com.flowpay.dto.request.RegisterRequest;
import com.flowpay.dto.response.LoginResponse;
import com.flowpay.dto.response.RegisterResponse;
import com.flowpay.service.AuthService;
import com.flowpay.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse result = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Đăng ký thành công",
                        result
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Đăng nhập thành công", result)
        );
    }
}
