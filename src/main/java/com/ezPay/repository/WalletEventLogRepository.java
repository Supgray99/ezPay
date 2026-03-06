package com.ezPay.repository;

import com.ezPay.model.WalletEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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

    // Replay to fetch specific events by their transaction IDs
    List<WalletEventLog> findByTransactionIdIn(List<String> transactionIds);

    // Replay to fetch all events in a time window for bulk replay
    /* streams rows from the database one at a time rather than loading them all in a list
        in case the time stamps are very long apart and there is a huge number of rows to be replayed
        thus preventing OOM
     */
    @Query("SELECT w FROM WalletEventLog w WHERE w.timestamp BETWEEN :from AND :to")
    Stream<WalletEventLog> streamByTimestampBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );


    boolean existsByTransactionId(String transactionId);
}
