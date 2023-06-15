package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;

@Data
public class ReplenishmentResponseDto {
    private Integer status;
    private String walletId;
    private Double amount;
    private CryptoCurrency currency;
    private Long timestamp;
}
