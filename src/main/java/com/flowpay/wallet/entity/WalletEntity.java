package com.flowpay.wallet.entity;


import com.flowpay.common.entity.AuditableEntity;
import com.flowpay.user.entity.UserEntity;
import com.flowpay.wallet.enums.CurrencyCode;
import com.flowpay.wallet.enums.WalletStatus;
import com.flowpay.wallet.enums.WalletType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class WalletEntity extends AuditableEntity {

    private static final BigDecimal DEFAULT_DAILY_TRANSFER_LIMIT = new BigDecimal("100000000.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_number", nullable = false, length = 20)
    private String walletNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 20)
    private WalletType walletType = WalletType.USER;


    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.VND;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "daily_transfer_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyTransferLimit = DEFAULT_DAILY_TRANSFER_LIMIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
