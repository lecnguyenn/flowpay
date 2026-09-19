package com.flowpay.transactions.repository;

import com.flowpay.transactions.entity.LedgerEntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, Long> {

    List<LedgerEntryEntity> findAllByTransaction_Id(Long transactionId);


    @EntityGraph(attributePaths = "transaction")
    Page<LedgerEntryEntity> findAllByWallet_IdOrderByCreatedAtDesc(Long walletId, Pageable pageable);
}
