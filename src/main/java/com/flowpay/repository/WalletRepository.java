package com.flowpay.repository;

import com.flowpay.entity.UserEntity;
import com.flowpay.entity.WalletEntity;
import com.flowpay.enums.CurrencyCode;
import com.flowpay.enums.WalletType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    Optional<WalletEntity> findByWalletNumber(String walletNumber);

    Optional<WalletEntity> findByOwner_IdAndCurrency(Long ownerId, CurrencyCode currency);

    boolean existsByWalletNumber(String walletNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT w
            FROM WalletEntity w
            WHERE w.id IN :walletIds
            ORDER BY w.id
            """)
    List<WalletEntity> findAllByIdForUpdate(@Param("walletIds") Collection<Long> walletIds);

    Optional<WalletEntity> findByOwner_Id(Long userId);

    Optional<WalletEntity> findByWalletTypeAndCurrency(WalletType walletType, CurrencyCode currency);
}
