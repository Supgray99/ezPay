package com.ezPay.service;

import com.ezPay.dto.AddMoneyRequestDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.Transaction;

public interface WalletService {
    Transaction addBalance(AddMoneyRequestDto requestDto);
    UserResponseDto getBalance(Long userId);
}
