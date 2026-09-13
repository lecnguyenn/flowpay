package com.flowpay.wallet.entity.response;


import lombok.Builder;

@Builder
public record WalletResponse (
        String walletNumber,
        String ownerName,
        String balance,
        String currency,
        String status,
        String dailyTransferLimit
) {
}
