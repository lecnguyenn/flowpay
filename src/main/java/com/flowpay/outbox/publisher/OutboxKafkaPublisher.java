package com.flowpay.outbox.publisher;


import com.flowpay.outbox.entity.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String EVENT_ID_HEADER = "event-id";
    private static final String EVENT_TYPE_HEADER = "event-type";
    private static final String AGGREGATE_TYPE_HEADER = "aggregate_type_header";



    @Value("${app.kafka.topics.transaction-completed}")
    private String transactionCompletedTopic;

    public void publish(OutboxEventEntity event) {
        String messageKey = String.valueOf(event.getAggregateId());
        String messageValue = event.getPayload().toString();

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                transactionCompletedTopic,
                messageKey,
                messageValue
        );

        addHeader(producerRecord, EVENT_ID_HEADER, event.getId().toString());
        addHeader(producerRecord, EVENT_TYPE_HEADER, event.getEventType());
        addHeader(producerRecord, AGGREGATE_TYPE_HEADER, event.getAggregateType());

        SendResult<String, String> result = kafkaTemplate.send(
                transactionCompletedTopic,
                    messageKey,
                    messageValue
        ).join();

        log.info("Published outbox event successfully: eventId={}, aggregateId={}, topic={}, partition={}, offset={}"
                , event.getId(), event.getAggregateId(), result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
    }

    private void addHeader(ProducerRecord<String, String> producerRecord, String headerName, String headerValue) {
        producerRecord.headers().add(headerName, headerValue.getBytes(StandardCharsets.UTF_8));
    }
}
