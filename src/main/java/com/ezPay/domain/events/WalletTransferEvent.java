package com.ezPay.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransferEvent {
    private String transactionId;
    private String senderId;
    private String receiverId;
    private double amount;
    private String status; // SUCCESS / FAILURE
    private LocalDateTime timestamp;
}
