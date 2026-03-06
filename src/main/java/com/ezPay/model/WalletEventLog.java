/*

WalletEventLog is the Kafka audit log — it records the event that was published to and consumed from Kafka.
It captures what happened at the messaging layer: who sent to whom, what the amount was, and critically, whether it succeeded or failed.
It's written by the Kafka consumer, not the wallet service directly.

*/



package com.ezPay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallet_event_log",
        indexes = {
                @Index(name = "idx_wel_sender_id",   columnList = "senderId"),
                @Index(name = "idx_wel_receiver_id", columnList = "receiverId"),
                @Index(name = "idx_wel_status",      columnList = "status"),
                @Index(name = "idx_wel_timestamp",   columnList = "timestamp"),
                @Index(name = "idx_wel_sender_status",   columnList = "senderId, status"),
                @Index(name = "idx_wel_receiver_status", columnList = "receiverId, status")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private String senderId;
    private String receiverId;
    private double amount;
    private String status;

    private LocalDateTime timestamp;
}
