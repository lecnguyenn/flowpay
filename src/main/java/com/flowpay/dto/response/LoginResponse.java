package com.flowpay.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
