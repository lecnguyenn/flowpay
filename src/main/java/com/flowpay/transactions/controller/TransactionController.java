package com.flowpay.transactions.controller;


import com.flowpay.common.response.ApiResponse;
import com.flowpay.common.response.PageResponse;
import com.flowpay.transactions.dto.response.TransactionHistoryResponse;
import com.flowpay.transactions.service.TransactionHistoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<TransactionHistoryResponse>>> getMyHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page không được nhỏ hơn 0")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size phải lơn hơn 0")
            @Max(value = 100, message = "Size không được lớn hơn 100")
            int size
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        PageResponse<TransactionHistoryResponse> response = transactionHistoryService.getHistory(userId, page,size);

        return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử giao dịch thành công", response));


    }
}
