package com.flowpay.transactions.service;

import com.flowpay.common.exception.AppException;
import com.flowpay.common.exception.ErrorCode;
import com.flowpay.event.TransactionCompletedEvent;
import com.flowpay.transactions.dto.request.TransferRequest;
import com.flowpay.transactions.dto.response.TransferResponse;
import com.flowpay.transactions.entity.LedgerEntryEntity;
import com.flowpay.transactions.repository.LedgerEntryRepository;
import com.flowpay.transactions.entity.WalletTransactionEntity;
import com.flowpay.transactions.enums.LedgerEntryType;
import com.flowpay.transactions.enums.TransactionStatus;
import com.flowpay.transactions.enums.TransactionType;
import com.flowpay.transactions.repository.WalletTransactionRepository;
import com.flowpay.utils.TransactionReferenceGenerator;
import com.flowpay.wallet.entity.WalletEntity;
import com.flowpay.wallet.enums.WalletStatus;
import com.flowpay.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransferResponse transfer(
            Long senderUserId,
            TransferRequest request
    ) {
        log.info("Transfer from senderUserId={}, amount={}, receiverUserId={}, idempotencyKey={}", senderUserId,
                request.amount(),
                request.receiverWalletId(),
                request.idempotencyKey()
        );
        var existingTransaction = transactionRepository.findByIdempotencyKey(request.idempotencyKey());
        if (existingTransaction.isPresent()) {
            return  toResponse(existingTransaction.get());
        }

        WalletEntity currentSenderWallet =
                walletRepository.findByOwner_Id(senderUserId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        if(currentSenderWallet.getId().equals(request.receiverWalletId())) {
            throw new AppException(ErrorCode.SAME_WALLET_TRANSFER);
        }

        List<Long> walletIds = List.of(currentSenderWallet.getId(), request.receiverWalletId());

        Map<Long, WalletEntity> lockedWallets = walletRepository.findAllByIdForUpdate(walletIds)
                .stream()
                .collect(Collectors.toMap(WalletEntity::getId, Function.identity()));
        WalletEntity senderWallet = lockedWallets.get(currentSenderWallet.getId());
        WalletEntity receiverWallet = lockedWallets.get(request.receiverWalletId());

        log.info("senderwallet = {}, receiverWallet = {}", senderWallet, receiverWallet);
        if(senderWallet == null || receiverWallet == null) {
            throw new AppException(ErrorCode.WALLET_NOT_FOUND);
        }

        existingTransaction = transactionRepository.findByIdempotencyKey(request.idempotencyKey());

        if(existingTransaction.isPresent()) {
            return toResponse(existingTransaction.get());
        }

        validateWallets(senderWallet, receiverWallet, request.amount());

        BigDecimal senderBalanceBefore = senderWallet.getBalance();
        BigDecimal receiverBalanceBefore = receiverWallet.getBalance();

        BigDecimal senderBalanceAfter = senderBalanceBefore.subtract(request.amount());
        BigDecimal receiverBalanceAfter = receiverBalanceBefore.add(request.amount());

        senderWallet.setBalance(senderBalanceAfter);
        receiverWallet.setBalance(receiverBalanceAfter);

        WalletTransactionEntity transaction = transactionRepository.save(
                WalletTransactionEntity.builder()
                        .referenceCode(TransactionReferenceGenerator.generator())
                        .idempotencyKey(request.idempotencyKey())
                        .type(TransactionType.TRANSFER)
                        .status(TransactionStatus.SUCCESS)
                        .amount(request.amount())
                        .currency(senderWallet.getCurrency())
                        .initiatedByUser(senderWallet.getOwner())
                        .description(request.description())
                        .build()
        );
        log.info("Transaction save successfully: referenceCode = {}", transaction.getReferenceCode());
        LedgerEntryEntity debitEntry = LedgerEntryEntity.builder()
                .transaction(transaction)
                .wallet(senderWallet)
                .entryType(LedgerEntryType.DEBIT)
                .amount(request.amount())
                .balanceBefore(senderBalanceBefore)
                .balanceAfter(senderBalanceAfter)
                .build();

        LedgerEntryEntity creditEntry = LedgerEntryEntity.builder()
                .transaction(transaction)
                .wallet(receiverWallet)
                .entryType(LedgerEntryType.CREDIT)
                .amount(request.amount())
                .balanceBefore(receiverBalanceBefore)
                .balanceAfter(receiverBalanceAfter)
                .build();

        walletRepository.saveAll(List.of(senderWallet, receiverWallet));
        ledgerEntryRepository.saveAll(List.of(debitEntry,creditEntry));

        eventPublisher.publishEvent(
                new TransactionCompletedEvent(
                        transaction.getId(),
                        transaction.getReferenceCode(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getCurrency()
                )
        );


        return TransferResponse.builder()
                .transactionId(transaction.getId())
                .referenceCode(transaction.getReferenceCode())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .senderWalletId(senderWallet.getId())
                .receiverWalletId(receiverWallet.getId())
                .build();

    }

    private TransferResponse toResponse(WalletTransactionEntity transaction) {
        List<LedgerEntryEntity> entries = ledgerEntryRepository.findAllByTransaction_Id(transaction.getId());

        LedgerEntryEntity debitEntry =
                entries.stream().filter(entry -> entry.getEntryType() == LedgerEntryType.DEBIT).findFirst().orElseThrow();

        LedgerEntryEntity creditEntry =
                entries.stream().filter(entry -> entry.getEntryType() == LedgerEntryType.CREDIT).findFirst().orElseThrow();

        return TransferResponse.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .referenceCode(transaction.getReferenceCode())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .receiverWalletId(creditEntry.getId())
                .senderWalletId(debitEntry.getId())
                .build();
    }

    private void validateWallets(
            WalletEntity senderWallet,
            WalletEntity receiverWallet,
            BigDecimal amount
    ) {
        if(senderWallet.getStatus() != WalletStatus.ACTIVE || receiverWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new AppException(ErrorCode.WALLET_NOT_ACTIVE);
        }
        if(senderWallet.getCurrency() != receiverWallet.getCurrency()) {
            throw new AppException(ErrorCode.SAME_WALLET_TRANSFER);
        }
        if(senderWallet.getBalance().compareTo(amount) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }
    }

//    private String genReferenceCode() {
//        String randomPart = UUID.randomUUID()
//                .toString()
//                .replace("-", "")
//                .substring(0,12)
//                .toUpperCase();
//        return "TXN-" + randomPart;
//    }


}
