package com.flowpay.repository;

import com.flowpay.entity.NotificationEntity;
import com.flowpay.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<NotificationEntity> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, NotificationStatus status);
}
