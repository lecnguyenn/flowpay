package com.flowpay.transactions.service;

import com.flowpay.event.TransactionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class TransactionEventListener {

    @Async("transactionEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Transaction completed: id={}, referenceCode={}, type={}, amount={}, currency={}",
                event.transactionId(),
                event.referenceCode(),
                event.type(),
                event.amount(),
                event.currency()
                );
    }
}
