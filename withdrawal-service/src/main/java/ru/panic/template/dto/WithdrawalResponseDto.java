package ru.panic.template.dto;

import lombok.Getter;
import lombok.Setter;
import ru.panic.template.enums.CryptoCurrency;

@Getter
@Setter
public class WithdrawalResponseDto {
    private Long userId;
    private String username;
    private String walletId;
    private Double amount;
    private Double gas;
    private CryptoCurrency currency;
    private Long timestamp;
}
