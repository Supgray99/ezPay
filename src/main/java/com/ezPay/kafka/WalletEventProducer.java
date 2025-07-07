package com.ezPay.kafka;

import com.ezPay.domain.events.WalletTransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WalletEventProducer {

    private static final String TOPIC = "wallet-transactions";

    @Autowired
    private KafkaTemplate<String, WalletTransferEvent> kafkaTemplate;

    public void publish(WalletTransferEvent event) {
        CompletableFuture<SendResult<String, WalletTransferEvent>> future =
                kafkaTemplate.send(TOPIC, event.getSenderId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka publish failed for event: {} | Reason: {}", event, ex.getMessage(), ex);
            } else {
                log.info("Kafka publish successful | Topic: {} | Partition: {} | Offset: {} | Event: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event);
            }
        });
    }
}