package com.ezPay.config;

import com.ezPay.domain.events.WalletTransferEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, WalletTransferEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // in the Docker setup this gets overridden
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "wallet-event-consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ezPay.domain.events"); // only trust deserialization of classes from the events package
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, WalletTransferEvent.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }


    /**
     * Primary listener factory — includes DLQ routing after 3 retries.
     * Used by the main wallet-transactions consumer.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WalletTransferEvent> kafkaListenerContainerFactory(
            KafkaTemplate<String, WalletTransferEvent> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, WalletTransferEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Retry + DLQ handler
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition("wallet-transactions.DLQ", 0)
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3) // 3 retries with 1 sec delay
        );

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }


    /**
     * Replay listener factory — no DLQ, no retries.
     * Replay consumers should handle failures explicitly and not create
     * secondary DLQ loops.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WalletTransferEvent> replayKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WalletTransferEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "wallet-replay-consumer");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ezPay.domain.events");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, WalletTransferEvent.class);

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(config));
        // No error handler — replay failures are logged and skipped, not re-queued
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0)));

        return factory;
    }
}