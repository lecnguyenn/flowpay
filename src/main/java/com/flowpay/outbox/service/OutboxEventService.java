package com.flowpay.outbox.service;


import com.flowpay.event.TransactionCompletedEvent;
import com.flowpay.outbox.entity.OutboxEventEntity;
import com.flowpay.outbox.enums.OutboxStatus;
import com.flowpay.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private static final String  AGGREGATE_TYPE = "WALLET_TRANSACTION";
    private static final String EVENT_TYPE = "TRANSACTION_COMPLETE";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveTransactionCompleted(TransactionCompletedEvent event) {
        OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                .aggregateType(AGGREGATE_TYPE)
                .aggregateId(event.transactionId())
                .eventType(EVENT_TYPE)
                .payload(objectMapper.valueToTree(event))
                .status(OutboxStatus.NEW)
                .retryCount(0)
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}
