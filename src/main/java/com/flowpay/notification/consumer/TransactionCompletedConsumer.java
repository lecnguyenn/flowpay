package com.flowpay.notification.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionCompletedConsumer {


    @KafkaListener(
            topics = "${app.kafka.topics.transaction-completed}",
            groupId = "${app.kafka.consumer-groups.notification}"
    )
    public void consumer(ConsumerRecord<String, String> record) {
        log.info("""
                RECEIVED TRANSACTION COMPLETED EVENT:
                key = {},
                value = {},
                topic = {},
                partition = {},\s
                offset = {}
                """,
                record.key(),
                record.value(),
                record.topic(),
                record.partition(),
                record.offset()
                );
    }
}
