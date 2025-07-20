package com.ezPay.service;

import com.ezPay.dto.PagedResponse;
import com.ezPay.dto.WalletEventLogDto;
import com.ezPay.model.WalletEventLog;
import com.ezPay.repository.WalletEventLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletEventLogService {

    @Autowired
    private WalletEventLogRepository repository;

    public PagedResponse<WalletEventLogDto> getUserTransactions(
            String userId, String status, LocalDateTime from, LocalDateTime to, Pageable pageable) {

        Page<WalletEventLog> logs;

        if (status == null || status.isEmpty()) {
            logs = repository.findBySenderIdOrReceiverIdAndTimestampBetween(userId, userId, from, to, pageable);
        } else {
            logs = repository.findBySenderIdOrReceiverIdAndStatusAndTimestampBetween(userId, userId, status, from, to, pageable);
        }

        List<WalletEventLogDto> content = logs.stream()
                .map(log -> new WalletEventLogDto(
                        log.getTransactionId(),
                        log.getSenderId(),
                        log.getReceiverId(),
                        log.getAmount(),
                        log.getStatus(),
                        log.getTimestamp()
                ))
                .toList();

        return new PagedResponse<>(
                content,
                logs.getNumber(),
                logs.getSize(),
                logs.getTotalElements(),
                logs.getTotalPages(),
                logs.isLast()
        );
    }
}