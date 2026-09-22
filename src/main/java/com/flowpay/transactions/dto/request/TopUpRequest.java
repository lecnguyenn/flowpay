package com.flowpay.transactions.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TopUpRequest (
        @NotNull(message = "Số tiền không được để trống")
        @Positive(message = "Số tiền phải lớn hơn 0")
        BigDecimal amount,
        @NotBlank(message = "Idempotency key không được để trống")
        String idempotencyKey,
        @Size(max = 255)
        String description
) {
}
