package com.flowpay.outbox.publisher;

import com.flowpay.outbox.entity.OutboxEventEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OutboxKafkaPublisherTests {
    @Test
    void publishesHeadersRequiredByTransactionConsumer() {
        var template = new CapturingKafkaTemplate();
        var publisher = new OutboxKafkaPublisher(template);
        String topic = "flowpay.transaction.completed.v1";
        ReflectionTestUtils.setField(publisher, "transactionCompletedTopic", topic);
        var event = OutboxEventEntity.builder()
                .aggregateId(42L)
                .aggregateType("TRANSACTION")
                .eventType("TRANSACTION_COMPLETED")
                .payload(new ObjectMapper().createObjectNode().put("transactionId", 42L))
                .build();

        publisher.publish(event);

        var record = template.record;
        assertNotNull(record);
        assertEquals(topic, record.topic());
        assertEquals("42", record.key());
        assertEquals(event.getPayload().toString(), record.value());
        assertEquals(event.getId().toString(), header(record, "event-id"));
        assertEquals(event.getEventType(), header(record, "event-type"));
        assertEquals(event.getAggregateType(), header(record, "aggregate_type_header"));
    }

    private String header(ProducerRecord<String, String> record, String name) {
        var header = record.headers().lastHeader(name);
        assertNotNull(header, "Missing header: " + name);
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    private static class CapturingKafkaTemplate extends KafkaTemplate<String, String> {
        private ProducerRecord<String, String> record;

        CapturingKafkaTemplate() {
            super(new DefaultKafkaProducerFactory<>(Map.of()));
        }

        @Override
        public CompletableFuture<SendResult<String, String>> send(ProducerRecord<String, String> record) {
            this.record = record;
            var metadata = new RecordMetadata(new TopicPartition(record.topic(), 0), 0, 0, 0, 0, 0);
            return CompletableFuture.completedFuture(new SendResult<>(record, metadata));
        }
    }
}
