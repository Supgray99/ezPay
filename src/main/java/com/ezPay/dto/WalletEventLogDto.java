package com.ezPay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletEventLogDto {
    private String transactionId;
    private String senderId;
    private String receiverId;
    private Double amount;
    private String status;
    private LocalDateTime timestamp;
}