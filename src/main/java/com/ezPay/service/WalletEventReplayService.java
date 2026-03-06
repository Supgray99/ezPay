
/**
 * This is the audit/replay engine of the system.
 * Its sole responsibility is to take events that were already processed and stored in the wallet_event_log table,
 * and republish them to a dedicated Kafka replay topic so downstream consumers can reprocess them.
 */

package com.ezPay.service;

import com.ezPay.domain.events.WalletTransferEvent;
import com.ezPay.model.WalletEventLog;
import com.ezPay.repository.WalletEventLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Service
public class WalletEventReplayService {

    private static final String REPLAY_TOPIC = "wallet-transactions.replay";

    private final WalletEventLogRepository eventLogRepository;
    private final KafkaTemplate<String, WalletTransferEvent> kafkaTemplate;

    public WalletEventReplayService(
            WalletEventLogRepository eventLogRepository,
            KafkaTemplate<String, WalletTransferEvent> kafkaTemplate
    ) {
        this.eventLogRepository = eventLogRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Replay specific events identified by their transaction IDs.
     */
    public int replayByTransactionIds(List<String> transactionIds) {
        List<WalletEventLog> events = eventLogRepository.findByTransactionIdIn(transactionIds);
        events.forEach(e -> publish(toEvent(e)));
        log.info("Replayed {} events by transaction ID", events.size());
        return events.size();
    }

    /**
     * Replay all events that occurred within a given time window.
     * Useful for re-driving a downstream consumer that missed a batch.
     */
    @Transactional
    public void replayByTimeWindow(LocalDateTime from, LocalDateTime to) {
        try (Stream<WalletEventLog> stream = eventLogRepository.streamByTimestampBetween(from, to)) {
            stream.forEach(e -> publish(toEvent(e)));
        }
        log.info("Replay triggered for window {} to {}", from, to);
    }

    private void publish(WalletTransferEvent event) {
        kafkaTemplate.send(REPLAY_TOPIC, event.getSenderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Replay publish failed for transactionId={}: {}", event.getTransactionId(), ex.getMessage());
                    } else {
                        log.info("Replayed event transactionId={} to topic={}", event.getTransactionId(), REPLAY_TOPIC);
                    }
                });
    }

    private WalletTransferEvent toEvent(WalletEventLog log) {
        return new WalletTransferEvent(
                log.getTransactionId(),
                log.getSenderId(),
                log.getReceiverId(),
                log.getAmount(),
                log.getStatus(),
                log.getTimestamp()
        );
    }
}
