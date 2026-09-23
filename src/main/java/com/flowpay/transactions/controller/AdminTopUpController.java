package com.flowpay.transactions.controller;


import com.flowpay.common.response.ApiResponse;
import com.flowpay.transactions.dto.request.TopUpRequest;
import com.flowpay.transactions.dto.response.TopUpResponse;
import com.flowpay.transactions.service.TopUpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/top-ups")
@RequiredArgsConstructor
@Validated
public class AdminTopUpController {

    private final TopUpService topUpService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<TopUpResponse>> topUp(
            @PathVariable @Positive(message = "userId phải lớn hơn 0") Long userId,
            @Valid @RequestBody TopUpRequest request
    ) {
        TopUpResponse response = topUpService.topUp(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Nạp tiền thành công", response)
        );
    }
}
