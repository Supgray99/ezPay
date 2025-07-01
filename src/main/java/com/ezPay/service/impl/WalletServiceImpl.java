package com.ezPay.service.impl;

import com.ezPay.dto.AddMoneyRequestDto;
import com.ezPay.dto.TransferRequestDto;
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

    @Override
    public UserResponseDto transfer(TransferRequestDto dto, String jwtUsername) {
        if (dto.getAmount() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        User sender = userRepository.findById(dto.getFromUserId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Validate token identity
        if (!sender.getUsername().equals(jwtUsername)) {
            throw new SecurityException("Token does not match sender");
        }

        User receiver = userRepository.findById(dto.getToUserId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getId() == (receiver.getId())) {
            throw new IllegalArgumentException("Cannot transfer to self");
        }

        if (sender.getBalance() < dto.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Update balances
        sender.setBalance(sender.getBalance() - dto.getAmount());
        receiver.setBalance(receiver.getBalance() + dto.getAmount());
        userRepository.save(sender);
        userRepository.save(receiver);

        // Log both transactions
        LocalDateTime now = LocalDateTime.now();

        Transaction debit = Transaction.builder()
                .userId(sender.getId())
                .amount(-dto.getAmount())
                .timestamp(now)
                .type(TransactionType.DEBIT)
                .description("Sent to user ID " + receiver.getId())
                .build();

        Transaction credit = Transaction.builder()
                .userId(receiver.getId())
                .amount(dto.getAmount())
                .timestamp(now)
                .type(TransactionType.CREDIT)
                .description("Received from user ID " + sender.getId())
                .build();

        transactionRepository.save(debit);
        transactionRepository.save(credit);

        // Return updated sender info
        return new UserResponseDto(sender.getId(), sender.getUsername(), sender.getBalance());
    }
}
