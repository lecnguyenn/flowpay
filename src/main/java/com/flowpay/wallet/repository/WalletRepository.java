package com.flowpay.wallet.repository;

import com.flowpay.wallet.entity.WalletEntity;
import com.flowpay.wallet.enums.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Optional<WalletEntity> findByWalletNumber(String walletNumber);

    Optional<WalletEntity> findByOwner_IdAndCurrency(Long ownerId, CurrencyCode currency);

    boolean existsByWalletNumber(String walletNumber);
}
