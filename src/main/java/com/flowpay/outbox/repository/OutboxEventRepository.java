package com.flowpay.outbox.repository;

import com.flowpay.outbox.entity.OutboxEventEntity;
import com.flowpay.outbox.enums.OutboxStatus;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT event
            FROM OutboxEventEntity event
            WHERE event.status = :status
            ORDER BY event.createdAt ASC
            """)
    List<OutboxEventEntity> findBatchForUpdate(@Param("status") OutboxStatus status, Pageable pageable);
}
