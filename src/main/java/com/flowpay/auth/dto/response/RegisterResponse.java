package com.flowpay.auth.dto.response;

import lombok.Builder;

@Builder
public record RegisterResponse(
        Long userId,
        String userName,
        String walletNumber
) { }
