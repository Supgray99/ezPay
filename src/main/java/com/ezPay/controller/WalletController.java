package com.ezPay.controller;

import com.ezPay.dto.AddMoneyRequestDto;
import com.ezPay.dto.TransferRequestDto;
import com.ezPay.dto.UserResponseDto;
import com.ezPay.model.Transaction;
import com.ezPay.service.WalletService;
import com.ezPay.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final TokenProvider tokenProvider;

    public WalletController(WalletService walletService, TokenProvider tokenProvider) {
        this.walletService = walletService;
        this.tokenProvider = tokenProvider;
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

//    @PostMapping("/transfer")
//    public ResponseEntity<UserResponseDto> transfer(@RequestBody TransferRequestDto dto) {
//        return ResponseEntity.ok(walletService.transfer(dto));
//    }

    @PostMapping("/transfer")
    public ResponseEntity<UserResponseDto> transfer(@RequestBody TransferRequestDto dto,
                                                    HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String jwtUsername = tokenProvider.extractUsername(token);

        return ResponseEntity.ok(walletService.transfer(dto, jwtUsername));
    }

}
