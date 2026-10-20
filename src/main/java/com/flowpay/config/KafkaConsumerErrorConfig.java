package com.flowpay.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConsumerErrorConfig {

    private static final long RETRY_INTERNAL_MILLISECONDS = 2_000L;
    private static final long MAX_RETRY_COUNT = 2L;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate,
                                                 @Value("${app.kafka.topics.transaction-completed-dtl}")
                                                 String deadLetterTopic
                                                 ){
        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(kafkaTemplate, (recorded,
                exception) -> new TopicPartition(deadLetterTopic, recorded.partition()));

        FixedBackOff fixedBackOff = new FixedBackOff(
                RETRY_INTERNAL_MILLISECONDS, MAX_RETRY_COUNT
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recover, fixedBackOff);

        errorHandler.setRetryListeners((record, exception, deliveryAttempt) -> log.warn("Retrying kafka message: " +
                "topic={}, partition={}, offset={}, attempt={}", record.topic(), record.partition(), record.offset(),
                deliveryAttempt, exception));

        return errorHandler;
    }
}
