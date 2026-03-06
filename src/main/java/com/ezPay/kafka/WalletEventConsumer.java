/**
 * WalletEventConsumer is the Kafka consumer side of the system.
 * It listens to topics, receives WalletTransferEvent messages, and persists them to the wallet_event_log table.
 * It has three listeners, each serving a distinct purpose — primary processing, dead letter handling, and replay processing.
 */


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


    /**
     * Replay consumer — processes events republished to the replay topic by
     * WalletEventReplayService. Uses a separate consumer group and a no-DLQ
     * factory so replayed events never re-enter the DLQ pipeline.
     */
    @KafkaListener(
            topics = "wallet-transactions.replay",
            groupId = "wallet-replay-consumer",
            containerFactory = "replayKafkaListenerContainerFactory"
    )
    public void handleReplay(WalletTransferEvent event) {
        log.info("REPLAY event received: transactionId={} status={}", event.getTransactionId(), event.getStatus());

        if (eventLogRepository.existsByTransactionId(event.getTransactionId())) {
            log.info("REPLAY skip — event already persisted: transactionId={}", event.getTransactionId());
            return;
        }

        WalletEventLog logEntry = new WalletEventLog();
        logEntry.setTransactionId(event.getTransactionId());
        logEntry.setSenderId(event.getSenderId());
        logEntry.setReceiverId(event.getReceiverId());
        logEntry.setAmount(event.getAmount());
        logEntry.setStatus(event.getStatus());
        logEntry.setTimestamp(event.getTimestamp());

        eventLogRepository.save(logEntry);
        log.info("REPLAY event persisted: {}", logEntry);
    }
}