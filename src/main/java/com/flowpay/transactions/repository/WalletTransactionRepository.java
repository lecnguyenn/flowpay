package com.flowpay.transactions.repository;

import com.flowpay.transactions.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {
    Optional<WalletTransactionEntity> findByReferenceCode(String referenceCode);

    Optional<WalletTransactionEntity> findByIdempotencyKey(String idempotencyKey);
}
