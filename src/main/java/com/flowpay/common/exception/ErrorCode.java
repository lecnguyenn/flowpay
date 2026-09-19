package com.flowpay.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    VALIDATION_ERROR("VALIDATION_ERROR", "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email đã tồn tại", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "Không tìm thấy user", HttpStatus.NOT_FOUND),
    WALLET_NOT_FOUND("WALLET_NOT_FOUND", "Không tìm thấy ví", HttpStatus.NOT_FOUND),
    INTERNAL_SERVICE_ERROR("INTERNAL_SERVICE_ERROR", "Hệ thống đang gặp sự cố", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Tên đăng nhâp hoặc mật khẩu không đúng", HttpStatus.BAD_REQUEST),
    USER_ACCOUNT_LOCKED("USER_ACCOUNT_LOCKED", "Tài khoản đã bị khoá", HttpStatus.FORBIDDEN),
    USER_ACCOUNT_DISABLED("USER_ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hoá", HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "refresh token không hợp lệ", HttpStatus.UNAUTHORIZED),

    WALLET_NOT_ACTIVE("WALLET_NOT_ACTIVE", "Ví không hoạt động", HttpStatus.BAD_REQUEST),
    SAME_WALLET_TRANSFER("TRANSFER_001", "Không thể chuyển tiền đến cùng 1 ví", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "Số dư không đủ để thực hiện", HttpStatus.BAD_REQUEST),
    CURRENCY_MISMATCH("CURRENCY_MISMATCH", "Hai ví không cùng loại tiền tệ", HttpStatus.BAD_REQUEST),
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND", "Không tìm thấy giao dịch", HttpStatus.NOT_FOUND);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
