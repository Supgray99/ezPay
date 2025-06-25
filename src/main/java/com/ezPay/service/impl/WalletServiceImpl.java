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
    public UserResponseDto transfer(TransferRequestDto dto) {
        if (dto.getFromUserId().equals(dto.getToUserId())) {
            throw new RuntimeException("Cannot transfer to the same user");
        }

        User sender = userRepository.findById(dto.getFromUserId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(dto.getToUserId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (sender.getBalance() < dto.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Perform transfer
        sender.setBalance(sender.getBalance() - dto.getAmount());
        receiver.setBalance(receiver.getBalance() + dto.getAmount());

        userRepository.save(sender);
        userRepository.save(receiver);

        // Log transactions
        transactionRepository.save(Transaction.builder()
                .userId(sender.getId())
                .amount(dto.getAmount())
                .timestamp(LocalDateTime.now())
                .type(TransactionType.DEBIT)
                .description("Transferred to " + receiver.getUsername())
                .build());

        transactionRepository.save(Transaction.builder()
                .userId(receiver.getId())
                .amount(dto.getAmount())
                .timestamp(LocalDateTime.now())
                .type(TransactionType.CREDIT)
                .description("Received from " + sender.getUsername())
                .build());

        return new UserResponseDto(sender.getId(), sender.getUsername(), sender.getBalance());
    }

}
