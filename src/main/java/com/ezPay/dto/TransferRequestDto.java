package com.ezPay.dto;

import lombok.Data;

@Data
public class TransferRequestDto {
    private Long fromUserId;
    private Long toUserId;
    private Double amount;
}
