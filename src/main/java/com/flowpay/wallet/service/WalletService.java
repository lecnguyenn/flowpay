package com.flowpay.wallet.service;


import com.flowpay.common.exception.AppException;
import com.flowpay.common.exception.ErrorCode;
import com.flowpay.wallet.entity.WalletEntity;
import com.flowpay.wallet.entity.response.WalletResponse;
import com.flowpay.wallet.enums.CurrencyCode;
import com.flowpay.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public WalletResponse getMyWallet(Long userId) {
        WalletEntity wallet =
                walletRepository.findByOwner_IdAndCurrency(userId, CurrencyCode.VND).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        return WalletResponse.builder()
                .walletNumber(wallet.getWalletNumber())
                .balance(wallet.getBalance().toPlainString())
                .currency(wallet.getCurrency().name())
                .status(wallet.getStatus().name())
                .dailyTransferLimit(wallet.getDailyTransferLimit().toPlainString())
                .ownerName(wallet.getOwner().getFullName())
                .build();
    }
}
