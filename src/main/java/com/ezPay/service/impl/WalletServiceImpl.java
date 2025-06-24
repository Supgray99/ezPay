package com.ezPay.service.impl;

import com.ezPay.dto.AddMoneyRequestDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.Transaction;
import com.ezPay.model.TransactionType;
import com.ezPay.model.User;
import com.ezPay.repository.TransactionRepository;
import com.ezPay.repository.UserRepository;
import com.ezPay.service.WalletService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction addBalance(AddMoneyRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBalance(user.getBalance() + requestDto.getAmount());
        userRepository.save(user);

        Transaction txn = new Transaction();
        txn.setUserId(user.getId());
        txn.setAmount(requestDto.getAmount());
        txn.setTimestamp(LocalDateTime.now());
        txn.setType(TransactionType.CREDIT);
        txn.setDescription("Wallet top-up");

        return transactionRepository.save(txn);
    }

    @Override
    public UserResponseDto getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponseDto(user.getId(), user.getUsername(), user.getBalance());
    }
}
