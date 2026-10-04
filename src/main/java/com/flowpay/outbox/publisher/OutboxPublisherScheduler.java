package com.flowpay.outbox.publisher;

import com.flowpay.outbox.service.OutboxPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxPublisherService outboxPublisherService;

    @Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay:5000}")
    public void publishOutboxEvents() {
        try {
            outboxPublisherService.publishBatch();
        } catch (Exception ex) {
            log.error("Outbox publisher scheduler failed", ex);
        }
    }

}
