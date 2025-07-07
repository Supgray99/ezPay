package com.ezPay.kafka;

import com.ezPay.domain.events.WalletTransferEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WalletEventConsumer {

    @KafkaListener(
            topics = "wallet-transactions",
            groupId = "wallet-event-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(WalletTransferEvent event) {
        log.info("? Received WalletTransferEvent: {}", event);
    }
}