package ru.panic.template.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.GameState;
import ru.panic.template.enums.GameType;

@Data
@NoArgsConstructor
public class GameDiceResponseDto {
    private GameType game;
    private GameState gameState;
    private Long userId;
    private String username;
    private Double amount;
    private CryptoCurrency currency;
    private Long timestamp;
}
