package com.flowpay.controller;


import com.flowpay.dto.response.ApiResponse;
import com.flowpay.dto.request.TransferRequest;
import com.flowpay.dto.response.TransferResponse;
import com.flowpay.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransferRequest request
            ) {
        Long senderUserId = Long.valueOf(jwt.getSubject());
        TransferResponse response = transferService.transfer(senderUserId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Chuyển tiền thành công", response)
        );
    }
}
