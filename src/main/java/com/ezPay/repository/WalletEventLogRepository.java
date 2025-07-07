package com.ezPay.repository;

import com.ezPay.model.WalletEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletEventLogRepository extends JpaRepository<WalletEventLog, Long> {}
