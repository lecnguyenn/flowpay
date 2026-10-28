package com.flowpay.dto.response;

import com.flowpay.enums.LedgerEntryType;
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
