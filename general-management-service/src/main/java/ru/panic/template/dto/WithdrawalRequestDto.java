package ru.panic.template.dto;

import lombok.Getter;
import ru.panic.template.enums.CryptoCurrency;

@Getter
public class WithdrawalRequestDto {
    private Long userId;
    private String username;
    private String walletId;
    private Double amount;
    private Double gas;
    private CryptoCurrency currency;
    private Long timestamp;
}
