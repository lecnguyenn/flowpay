package com.flowpay.outbox.listener;


import com.flowpay.event.TransactionCompletedEvent;
import com.flowpay.outbox.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxEventService outboxEventService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        outboxEventService.saveTransactionCompleted(event);
    }

}
