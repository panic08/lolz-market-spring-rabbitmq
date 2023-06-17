package ru.panic.template.entity;

import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.GameState;
import ru.panic.template.enums.GameType;
@Data
public class GameResponseDto {
    private Long userId;
    private String username;
    private Double amount;
    private CryptoCurrency currency;
    private GameState state;
    private GameType type;
    private Long timestamp;
}
