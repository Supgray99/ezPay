package com.ezPay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class TransferRequestDto {

    @NotNull
    private Long toUserId;

    @NotNull
    @Min(1)
    private Double amount;
}
