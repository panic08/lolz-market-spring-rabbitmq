package ru.panic.template.dto;

import lombok.Getter;
import ru.panic.template.enums.CryptoCurrency;

@Getter
public class GameDiceRequestDto {
    private String username;
    private Double amount;
    private Integer percent;
    private CryptoCurrency currency;
}
