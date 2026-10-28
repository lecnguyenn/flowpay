package com.flowpay.dto.response;


import com.flowpay.enums.TransactionStatus;
import com.flowpay.enums.TransactionType;
import com.flowpay.enums.CurrencyCode;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TransactionDetailResponse(
        Long transactionId,
        String referenceCode,
        TransactionType type,
        TransactionStatus status,
        BigDecimal amount,
        CurrencyCode currency,
        String description,
        Instant createdAt,
        List<LedgerEntryResponse> entries
) {
}
