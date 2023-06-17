package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.GameState;
import ru.panic.template.enums.GameType;

@Entity
@Table(name = "games")
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoCurrency currency;

    @Column(name = "game", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType game;

    @Column(name = "game_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameState gameState;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
}
