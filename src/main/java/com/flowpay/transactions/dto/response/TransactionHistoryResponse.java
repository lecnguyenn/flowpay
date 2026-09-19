package com.flowpay.transactions.dto.response;

import com.flowpay.transactions.enums.LedgerEntryType;
import com.flowpay.transactions.enums.TransactionStatus;
import com.flowpay.transactions.enums.TransactionType;
import com.flowpay.wallet.enums.CurrencyCode;
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
