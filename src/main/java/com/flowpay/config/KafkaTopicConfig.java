package com.flowpay.config;



import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public NewTopic transactionCompletedTopic(@Value("${app.kafka.topics.transaction-completed}") String topicName) {
        return TopicBuilder
                .name(topicName)
                .partitions(3)
                .replicas(1)
                .build();

    }
}
