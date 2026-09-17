package com.flowpay.transactions.dto.response;

import com.flowpay.transactions.enums.TransactionStatus;
import com.flowpay.wallet.enums.CurrencyCode;
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
