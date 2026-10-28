package com.flowpay.repository;

import com.flowpay.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {
    Optional<WalletTransactionEntity> findByReferenceCode(String referenceCode);

    Optional<WalletTransactionEntity> findByIdempotencyKey(String idempotencyKey);

    @Query("""
            SELECT tx
            FROM WalletTransactionEntity tx
            WHERE tx.id = :transactionId
                AND EXISTS (
                    SELECT ledger.id
                    FROM LedgerEntryEntity ledger
                    WHERE ledger.transaction = tx
                        AND ledger.wallet.owner.id = :userId
                )
            """)
    Optional<WalletTransactionEntity> findByIdAndParticipantUserId(@Param("userId") Long userId, @Param(
            "transactionId") Long transactionId);
}
