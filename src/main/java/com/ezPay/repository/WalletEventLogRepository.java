package com.ezPay.repository;

import com.ezPay.model.WalletEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WalletEventLogRepository extends JpaRepository<WalletEventLog, Long> {

    Page<WalletEventLog> findBySenderIdOrReceiverId(
            String senderId,
            String receiverId,
            Pageable pageable
    );

    Page<WalletEventLog> findBySenderIdOrReceiverIdAndStatus(
            String senderId,
            String receiverId,
            String status,
            Pageable pageable
    );

    Page<WalletEventLog> findBySenderIdOrReceiverIdAndTimestampBetween(
            String senderId,
            String receiverId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<WalletEventLog> findBySenderIdOrReceiverIdAndStatusAndTimestampBetween(
            String senderId,
            String receiverId,
            String status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

}
