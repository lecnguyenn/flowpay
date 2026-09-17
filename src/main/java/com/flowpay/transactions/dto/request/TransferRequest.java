package com.flowpay.transactions.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Ví nhận không được để trống")
        Long receiverWalletId,

        @NotNull(message = "Số tiền không được để trống")
        @Positive(message = "Số tiền phải lớn hơn 0")
        @Digits(integer = 19, fraction = 0, message = "Số tiền không được có dấu thập phân")
        BigDecimal amount,
        String idempotencyKey,

        @Size(max = 255)
        String description
) {
}
