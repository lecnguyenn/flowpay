package com.flowpay.transactions.service;

import com.flowpay.common.exception.AppException;
import com.flowpay.common.exception.ErrorCode;
import com.flowpay.transactions.dto.request.TopUpRequest;
import com.flowpay.transactions.dto.response.TopUpResponse;
import com.flowpay.transactions.entity.LedgerEntryEntity;
import com.flowpay.transactions.entity.WalletTransactionEntity;
import com.flowpay.transactions.enums.LedgerEntryType;
import com.flowpay.transactions.enums.TransactionType;
import com.flowpay.transactions.repository.LedgerEntryRepository;
import com.flowpay.transactions.repository.WalletTransactionRepository;
import com.flowpay.wallet.entity.WalletEntity;
import com.flowpay.wallet.enums.CurrencyCode;
import com.flowpay.wallet.enums.WalletType;
import com.flowpay.wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TopUpService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public TopUpResponse topUp(Long userId, TopUpRequest request) {
        var existingTransaction = transactionRepository.findByIdempotencyKey(request.idempotencyKey());

        if(existingTransaction.isPresent()) {
            return handleExisting(existingTransaction.get(), userId, request);
        }

        WalletEntity currentUserWallet =
                walletRepository.findByOwner_Id(userId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        WalletEntity currentSystemWallet = walletRepository.findByWalletTypeAndCurrency(WalletType.SYSTEM,
                CurrencyCode.VND).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        List<Long> walletIds = List.of(currentSystemWallet.getId(), currentSystemWallet.getId());

        Map<Long, WalletEntity> lockedWallets = walletRepository.findAllByIdForUpdate(walletIds)
                .stream()
                .collect(Collectors.toMap(
                        WalletEntity::getId,
                        Function.identity()
                ));




    }

    private TopUpResponse handleExisting(WalletTransactionEntity transaction, Long userId, TopUpRequest request) {
        boolean invalidTransaction =
                transaction.getType() != TransactionType.DEPOSIT ||
                        transaction.getInitiatedByUser().getId().equals(userId) ||
                        transaction.getAmount().compareTo(request.amount()) != 0;

        if(invalidTransaction) {
            throw new AppException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT);
        }

        LedgerEntryEntity creditEntry = ledgerEntryRepository.findAllByTransaction_Id(transaction.getId())
                .stream()
                .filter(entry -> entry.getEntryType() == LedgerEntryType.CREDIT)
                .findFirst()
                .orElseThrow();

        return TopUpResponse.builder()
                .currency(transaction.getCurrency())
                .referenceCode(transaction.getReferenceCode())
                .status(transaction.getStatus())
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .walletId(creditEntry.getWallet().getId())
                .balanceAfter(creditEntry.getBalanceAfter())

                .build();
    };
}
