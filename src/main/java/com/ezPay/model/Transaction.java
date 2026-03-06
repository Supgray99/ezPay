/*

Transaction is the financial ledger of the system.
Every time money moves — whether a wallet top-up or a transfer between two users — one or more Transaction rows are written to the transactions table.
It is a permanent, append-only record. Rows are never updated or deleted, only inserted.

*/


package com.ezPay.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_user_id", columnList = "userId"),
                @Index(name = "idx_transactions_timestamp", columnList = "timestamp"),
                @Index(name = "idx_transactions_user_timestamp", columnList = "userId, timestamp")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

}