package com.flowpay.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Tên đăng nhập không được bỏ trống")
        @Size(min = 4, max = 50, message = "Tên đăng nhập phải có từ 4 dến 50 ký tự ")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Tên đăng  chỉ được chứa chữ, số, dấu chấm, gạch dưới và " +
                "gạch ngang")
        String username,

        @NotBlank(message = "email không được để trống")
        @Size(max=150, message = "Email không được vượt quá 150 ký tự")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, max = 72, message = "Mật khẩu phải có từ 6 đến 72 ký tự")
        String password,

        @NotBlank(message = "Họ tên không được để trống")
        String fullName
) {
}
