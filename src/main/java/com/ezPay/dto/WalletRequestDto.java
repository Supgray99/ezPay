package com.ezPay.dto;

import lombok.Data;

@Data
public class WalletRequestDto {
    private Long userId;
    private Double amount;
}
