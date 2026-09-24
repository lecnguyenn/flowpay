package com.flowpay.event;

import com.flowpay.transactions.enums.TransactionType;
import com.flowpay.wallet.enums.CurrencyCode;

import java.math.BigDecimal;

public record TransactionCompletedEvent(
        Long transactionId,
        String referenceCode,
        TransactionType type,
        BigDecimal amount,
        CurrencyCode currency
) {
}
