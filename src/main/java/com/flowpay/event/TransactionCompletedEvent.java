package com.flowpay.event;

import com.flowpay.enums.TransactionType;
import com.flowpay.enums.CurrencyCode;

import java.math.BigDecimal;

public record TransactionCompletedEvent(
        Long transactionId,
        String referenceCode,
        TransactionType type,
        BigDecimal amount,
        CurrencyCode currency
) {
}
