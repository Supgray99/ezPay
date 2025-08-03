package com.ezPay.lock;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class WalletLockManager {

    private final ConcurrentMap<UUID, Object> lockMap = new ConcurrentHashMap<>();

    public Object getLockForWallet(UUID walletId) {
        return lockMap.computeIfAbsent(walletId, id -> new Object());
    }

    // Optional cleanup method (if desired)
    public void removeLock(UUID walletId) {
        lockMap.remove(walletId);
    }
}
