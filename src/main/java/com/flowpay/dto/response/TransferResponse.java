package com.flowpay.dto.response;

import com.flowpay.enums.TransactionStatus;
import com.flowpay.enums.CurrencyCode;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferResponse (
        Long transactionId,
        String referenceCode,
        TransactionStatus status,
        BigDecimal amount,
        CurrencyCode currency,
        Long senderWalletId,
        Long receiverWalletId
){
}
