package ru.panic.template.dto;

import lombok.Getter;
import ru.panic.template.enums.CryptoCurrency;

@Getter
public class GameRequestDto {
    private Long userId;
    private String username;
    private Double amount;
    private CryptoCurrency currency;

}
