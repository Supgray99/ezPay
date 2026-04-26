package com.ezPay.controller;

import com.ezPay.dto.*;
import com.ezPay.model.Transaction;
import com.ezPay.service.WalletEventLogService;
import com.ezPay.service.WalletService;
import com.ezPay.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final TokenProvider tokenProvider;
    private final WalletEventLogService walletEventLogService;

    public WalletController(
            WalletService walletService,
            TokenProvider tokenProvider,
            WalletEventLogService walletEventLogService
    ) {
        this.walletService = walletService;
        this.tokenProvider = tokenProvider;
        this.walletEventLogService = walletEventLogService;
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

    @PostMapping("/transfer")
    public ResponseEntity<UserResponseDto> transfer(@Valid @RequestBody TransferRequestDto dto,
                                                    HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String jwtUsername = tokenProvider.extractUsername(token);

        return ResponseEntity.ok(walletService.transfer(dto, jwtUsername));
    }

    @GetMapping("/transactions")
    public ResponseEntity<PagedResponse<WalletEventLogDto>> getTransactions(
            @RequestParam String userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (from == null) from = LocalDateTime.now().minusMonths(1); // sensible default
        if (to == null) to = LocalDateTime.now();

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        return ResponseEntity.ok(walletEventLogService.getUserTransactions(userId, status, from, to, pageable));
    }

}
