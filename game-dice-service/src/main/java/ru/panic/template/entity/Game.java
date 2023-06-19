package ru.panic.template.entity;

import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.GameState;
import ru.panic.template.enums.GameType;
@Data
public class Game {
    private Long id;
    private Long userId;
    private String username;
    private Double amount;
    private CryptoCurrency currency;
    private GameType game;
    private GameState gameState;
    private Long timestamp;
}
