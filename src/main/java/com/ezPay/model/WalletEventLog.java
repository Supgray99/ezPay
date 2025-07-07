package com.ezPay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_event_log")
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
