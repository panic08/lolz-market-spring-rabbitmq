package ru.panic.template.service;

import ru.panic.template.dto.GameDiceRequestDto;
import ru.panic.template.dto.GameDiceResponseDto;

public interface GameDiceService {
    GameDiceResponseDto handleDice(String jwtToken, GameDiceRequestDto request);
}
