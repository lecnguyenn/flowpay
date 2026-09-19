package com.flowpay.transactions.dto.response;


import com.flowpay.transactions.enums.TransactionStatus;
import com.flowpay.transactions.enums.TransactionType;
import com.flowpay.wallet.enums.CurrencyCode;
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
