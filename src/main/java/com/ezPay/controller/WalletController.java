package com.ezPay.controller;

import com.ezPay.dto.AddMoneyRequestDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.Transaction;
import com.ezPay.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/add")
    public ResponseEntity<Transaction> addMoney(@RequestBody AddMoneyRequestDto request) {
        Transaction transaction = walletService.addBalance(request);

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<UserResponseDto> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }
}
