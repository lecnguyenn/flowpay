package com.flowpay.kafka.repository;

import com.flowpay.kafka.entity.ProcessedKafkaEntity;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedKafkaEventRepository extends JpaRepository<ProcessedKafkaEntity, Long> {

    boolean existsByEventIdAndConsumerGroup(UUID eventId, String consumerGroup);

}
