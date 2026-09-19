package com.flowpay.transactions.dto.response;

import com.flowpay.transactions.enums.LedgerEntryType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LedgerEntryResponse(
        Long walletId,
        BigDecimal amount,
        LedgerEntryType entryType,
        BigDecimal amountBefore,
        BigDecimal amountAfter
) {
}
