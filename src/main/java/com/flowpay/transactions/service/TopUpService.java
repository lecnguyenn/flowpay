package com.flowpay.transactions.service;

import com.flowpay.common.exception.AppException;
import com.flowpay.common.exception.ErrorCode;
import com.flowpay.transactions.dto.request.TopUpRequest;
import com.flowpay.transactions.dto.response.TopUpResponse;
import com.flowpay.transactions.entity.LedgerEntryEntity;
import com.flowpay.transactions.entity.WalletTransactionEntity;
import com.flowpay.transactions.enums.LedgerEntryType;
import com.flowpay.transactions.enums.TransactionStatus;
import com.flowpay.transactions.enums.TransactionType;
import com.flowpay.transactions.repository.LedgerEntryRepository;
import com.flowpay.transactions.repository.WalletTransactionRepository;
import com.flowpay.utils.TransactionReferenceGenerator;
import com.flowpay.wallet.entity.WalletEntity;
import com.flowpay.wallet.enums.CurrencyCode;
import com.flowpay.wallet.enums.WalletStatus;
import com.flowpay.wallet.enums.WalletType;
import com.flowpay.wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TopUpService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public TopUpResponse topUp(Long userId, TopUpRequest request) {
        log.info("Tranfer amount from system to {} with amount = {}", userId, request.amount());
        var existingTransaction = transactionRepository.findByIdempotencyKey(request.idempotencyKey());

        if(existingTransaction.isPresent()) {
            return handleExisting(existingTransaction.get(), userId, request);
        }

        WalletEntity currentUserWallet =
                walletRepository.findByOwner_Id(userId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        WalletEntity currentSystemWallet = walletRepository.findByWalletTypeAndCurrency(WalletType.SYSTEM,
                CurrencyCode.VND).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        List<Long> walletIds = List.of(currentSystemWallet.getId(), currentUserWallet.getId());

        Map<Long, WalletEntity> lockedWallets = walletRepository.findAllByIdForUpdate(walletIds)
                .stream()
                .collect(Collectors.toMap(
                        WalletEntity::getId,
                        Function.identity()
                ));

        WalletEntity userWallet = lockedWallets.get(currentUserWallet.getId());
        WalletEntity systemWallet = lockedWallets.get(currentSystemWallet.getId());

        if(userWallet == null || systemWallet == null) {
            throw new AppException(ErrorCode.WALLET_NOT_FOUND);
        }

        existingTransaction = transactionRepository.findByIdempotencyKey(request.idempotencyKey());

        if(existingTransaction.isPresent()) {
            return handleExisting(existingTransaction.get(), userId, request);
        }

        validateWallets(systemWallet, userWallet, request.amount());

        BigDecimal systemBalanceBefore = systemWallet.getBalance();
        BigDecimal userBalanceBefore = userWallet.getBalance();

        BigDecimal systemBalanceAfter = systemBalanceBefore.subtract(request.amount());
        BigDecimal userBalanceAfter = userBalanceBefore.add(request.amount());

        systemWallet.setBalance(systemBalanceAfter);
        userWallet.setBalance(userBalanceAfter);

        WalletTransactionEntity transaction = transactionRepository.save(
                WalletTransactionEntity.builder()
                        .referenceCode(TransactionReferenceGenerator.generator())
                        .idempotencyKey(request.idempotencyKey())
                        .type(TransactionType.DEPOSIT)
                        .status(TransactionStatus.SUCCESS)
                        .amount(request.amount())
                        .currency(userWallet.getCurrency())
                        .initiatedByUser(userWallet.getOwner())
                        .description(request.description())
                        .build()
        );

        LedgerEntryEntity systemDebit = LedgerEntryEntity.builder()
                .transaction(transaction)
                .wallet(systemWallet)
                .entryType(LedgerEntryType.DEBIT)
                .amount(request.amount())
                .balanceBefore(systemBalanceBefore)
                .balanceAfter(systemBalanceAfter)
                .build();

        LedgerEntryEntity userCredit = LedgerEntryEntity.builder()
                .transaction(transaction)
                .wallet(userWallet)
                .entryType(LedgerEntryType.CREDIT)
                .amount(request.amount())
                .balanceAfter(userBalanceAfter)
                .balanceBefore(userBalanceBefore)
                .build();

        walletRepository.saveAll(List.of(systemWallet, userWallet));
        ledgerEntryRepository.saveAll(List.of(systemDebit,userCredit));
        return TopUpResponse.builder()
                .transactionId(transaction.getId())
                .currency(transaction.getCurrency())
                .referenceCode(transaction.getReferenceCode())
                .balanceAfter(userBalanceAfter)
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .walletId(userWallet.getId())
                .status(transaction.getStatus())
                .build();


    }

    private TopUpResponse handleExisting(WalletTransactionEntity transaction, Long userId, TopUpRequest request) {
        boolean invalidTransaction =
                transaction.getType() != TransactionType.DEPOSIT ||
                        !transaction.getInitiatedByUser().getId().equals(userId) ||
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

    private void validateWallets(WalletEntity systemWallet, WalletEntity userWallet, BigDecimal amount) {
        if(systemWallet.getStatus() != WalletStatus.ACTIVE || userWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new AppException(ErrorCode.WALLET_NOT_ACTIVE);
        }

        if(systemWallet.getCurrency() != userWallet.getCurrency()){
            throw new AppException(ErrorCode.CURRENCY_MISMATCH);
        }

        if(systemWallet.getBalance().compareTo(amount) < 0 ) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }
    }
}
