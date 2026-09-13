package com.flowpay.security.repository;

import com.flowpay.security.refresh.entity.RefreshTokenEntity;
import com.flowpay.security.refresh.enums.RefreshTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findAllByUser_IdAndStatus(Long userId, RefreshTokenStatus status);
}
