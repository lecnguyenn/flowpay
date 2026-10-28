package com.flowpay.service;

import com.flowpay.exception.AppException;
import com.flowpay.exception.ErrorCode;
import com.flowpay.dto.response.PageResponse;
import com.flowpay.dto.response.LedgerEntryResponse;
import com.flowpay.dto.response.TransactionDetailResponse;
import com.flowpay.dto.response.TransactionHistoryResponse;
import com.flowpay.entity.LedgerEntryEntity;
import com.flowpay.entity.WalletTransactionEntity;
import com.flowpay.repository.LedgerEntryRepository;
import com.flowpay.repository.WalletTransactionRepository;
import com.flowpay.entity.WalletEntity;
import com.flowpay.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final WalletRepository walletRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final WalletTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public PageResponse<TransactionHistoryResponse> getHistory(Long userId, int page, int size) {
        log.info("Getting transaction history , userId={}", userId);
        WalletEntity wallet =
                walletRepository.findByOwner_Id(userId).orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<TransactionHistoryResponse> history = ledgerEntryRepository
                .findAllByWallet_IdOrderByCreatedAtDesc(wallet.getId(), pageRequest)
                .map(this::toResponse);
        return PageResponse.from(history);
    }

    private TransactionHistoryResponse toResponse(LedgerEntryEntity ledgerEntryEntity) {
        WalletTransactionEntity transaction = ledgerEntryEntity.getTransaction();

        return TransactionHistoryResponse.builder()
                .transactionId(transaction.getId())
                .referenceCode(transaction.getReferenceCode())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .balanceAfter(ledgerEntryEntity.getBalanceAfter())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .direction(ledgerEntryEntity.getEntryType())
                .createdAt(ledgerEntryEntity.getCreatedAt())
                .build();
    }

    public TransactionDetailResponse getDetail(Long userId, Long transactionId) {
        WalletTransactionEntity transaction = transactionRepository.findByIdAndParticipantUserId(userId,
                transactionId).orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        List<LedgerEntryResponse> entries =
                ledgerEntryRepository.findAllByTransaction_Id(transactionId).stream().map(entry -> new LedgerEntryResponse(
                        entry.getWallet().getId(),
                        entry.getAmount(),
                        entry.getEntryType(),
                        entry.getBalanceBefore(),
                        entry.getBalanceAfter()
                )).toList();
        return TransactionDetailResponse.builder()
                .transactionId(transaction.getId())
                .referenceCode(transaction.getReferenceCode())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .entries(entries)
                .build();
    }
}
