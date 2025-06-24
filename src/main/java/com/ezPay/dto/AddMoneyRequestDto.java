package com.ezPay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMoneyRequestDto {
    private Long userId;
    private Double amount;
}
