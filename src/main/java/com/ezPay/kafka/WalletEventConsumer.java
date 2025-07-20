package com.ezPay.kafka;

import com.ezPay.domain.events.WalletTransferEvent;
import com.ezPay.model.WalletEventLog;
import com.ezPay.repository.WalletEventLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WalletEventConsumer {

    private final WalletEventLogRepository eventLogRepository;

    public WalletEventConsumer(WalletEventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    @KafkaListener(
            topics = "wallet-transactions",
            groupId = "wallet-event-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(WalletTransferEvent event) {

        log.info("Received WalletTransferEvent: {}", event);

        // manually throwing errors for already published events to test DLQ
//        if (event.getAmount() > 0) {
//            throw new RuntimeException("Simulated failure for testing DLQ");
//        }

        WalletEventLog logEntry = new WalletEventLog();
        logEntry.setTransactionId(event.getTransactionId());
        logEntry.setSenderId(event.getSenderId());
        logEntry.setReceiverId(event.getReceiverId());
        logEntry.setAmount(event.getAmount());
        logEntry.setStatus(event.getStatus());
        logEntry.setTimestamp(event.getTimestamp());

        eventLogRepository.save(logEntry);
        log.info("Event persisted to DB: {}", logEntry);
    }

    @KafkaListener(
            topics = "wallet-transactions.DLQ",
            groupId = "dlq-handler"
    )
    public void handleFailed(WalletTransferEvent failedEvent) {
        log.error("DLQ EVENT RECEIVED: {}", failedEvent);
    }
}