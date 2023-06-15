package ru.panic.template.dto;

import lombok.Getter;
import ru.panic.template.enums.CryptoCurrency;
@Getter
public class ReplenishmentHashRequestDto {
    private String username;
    private Double amount;
    private CryptoCurrency cryptoCurrency;
    private Long timestamp;
}
