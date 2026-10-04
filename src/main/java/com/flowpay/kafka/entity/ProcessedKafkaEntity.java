package com.flowpay.kafka.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "processed_kafka_events", uniqueConstraints = {
        @UniqueConstraint(name = "uk_processed_event_consumer", columnNames = {"event_id", "consumer_group"})
})
public class ProcessedKafkaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "consumer_group", nullable = false, length = 200)
    private String consumerGroup;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 200)
    private String topic;

    @Column(name = "partition_number", nullable = false)
    private Integer partitionNumber;

    @Column(name = "kafka_offset", nullable = false)
    private Long kafkaOffset;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @PrePersist
    public void prePersist() {
        if(processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }

}
