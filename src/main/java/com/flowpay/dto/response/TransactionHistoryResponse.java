package com.flowpay.dto.response;

import com.flowpay.enums.LedgerEntryType;
import com.flowpay.enums.TransactionStatus;
import com.flowpay.enums.TransactionType;
import com.flowpay.enums.CurrencyCode;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionHistoryResponse(
        Long transactionId,
        String referenceCode,
        TransactionType type,
        TransactionStatus status,
        LedgerEntryType direction,
        BigDecimal amount,
        CurrencyCode currency,
        BigDecimal balanceAfter,
        String description,
        LocalDateTime createdAt


) {
}
