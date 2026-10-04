package com.flowpay.outbox.service;


import com.flowpay.outbox.entity.OutboxEventEntity;
import com.flowpay.outbox.enums.OutboxStatus;
import com.flowpay.outbox.publisher.OutboxKafkaPublisher;
import com.flowpay.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRY_COUNT = 3;

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxKafkaPublisher outboxKafkaPublisher;

    @Transactional
    public void publishBatch() {
        List<OutboxEventEntity> events = outboxEventRepository.findBatchForUpdate(OutboxStatus.NEW, PageRequest.of(0,
                BATCH_SIZE));

        if(events.isEmpty()) {
            return;
        }

        log.info("Found {} outbox events to publish", events.size());

        for(OutboxEventEntity event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEventEntity event) {
        try {
            outboxKafkaPublisher.publish(event);
            event.setStatus(OutboxStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
        } catch (Exception ex) {
            int retryCount = event.getRetryCount() + 1;
            event.setRetryCount(retryCount);
            event.setLastError(buildErrorMessage(ex));

            if(retryCount >= MAX_RETRY_COUNT) {
                event.setStatus(OutboxStatus.FAILED);
            }

            log.error("Failed to publish outbox event: eventId={} retryCount={}", event.getId(), retryCount, ex);
        }
    }

    private String buildErrorMessage(Exception ex) {
        return ex.getClass().getSimpleName() + ": " + ex.getMessage();
    }

}
