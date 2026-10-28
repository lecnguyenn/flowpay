package com.flowpay.service;


import com.flowpay.entity.ProcessedKafkaEntity;
import com.flowpay.repository.ProcessedKafkaEventRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionEventProcessor {

    private static final String EVENT_ID_HEADER = "event-id";
    private static final String EVENT_TYPE_HEADER = "event-type";

    private final ProcessedKafkaEventRepository processedKafkaEventRepository;

    @Value("${app.kafka.consumer-groups.notification}")
    private String consumerGroup;

    public void process(ConsumerRecord<String, String> record) {
        UUID eventId = UUID.fromString(getRequiredHeader(record, EVENT_ID_HEADER));
        String eventType = getRequiredHeader(record, EVENT_TYPE_HEADER);
        boolean alreadyProcessed = processedKafkaEventRepository.existsByEventIdAndConsumerGroup(eventId,consumerGroup);

        if(alreadyProcessed){
            log.info("Skipping duplicated event: eventId={}, consumerGroup={}", eventId, consumerGroup);
            return;
        }
        processTransactionCompleted(record.value());

        ProcessedKafkaEntity processEvent = ProcessedKafkaEntity.builder()
                .eventId(eventId)
                .consumerGroup(consumerGroup)
                .eventType(eventType)
                .topic(record.topic())
                .partitionNumber(record.partition())
                .kafkaOffset(record.offset())
                .build();

        processedKafkaEventRepository.save(processEvent);

        log.info("Processed Kafka successfully: eventId={}, consumerGroup={}, partition={}, offset={}", eventId,
                consumerGroup, record.partition(), record.offset());
    }

    private String getRequiredHeader(ConsumerRecord<String, String> record, String headerName) {
        Header header = record.headers().lastHeader(headerName);
        if(header == null) {
            throw new IllegalArgumentException("Missing required Kafka event: " + headerName);
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    private void processTransactionCompleted(String payload) {
        log.info("Processing transaction completed payload: {}", payload);
    }
}
