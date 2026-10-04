package com.flowpay.outbox.publisher;


import com.flowpay.outbox.entity.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;


    @Value("${app.kafka.topics.transaction-completed}")
    private String transactionCompletedTopic;

    public void publish(OutboxEventEntity event) {
        String messageKey = String.valueOf(event.getAggregateId());
        String messageValue = event.getPayload().toString();

        SendResult<String, String> result = kafkaTemplate.send(
                transactionCompletedTopic,
                    messageKey,
                    messageValue
        ).join();

        log.info("Published outbox event successfully: eventId={}, aggregateId={}, topic={}, partition={}, offset={}"
                , event.getId(), event.getAggregateId(), result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
    }
}
