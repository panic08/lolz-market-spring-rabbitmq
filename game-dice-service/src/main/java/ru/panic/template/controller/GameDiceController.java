package ru.panic.template.controller;

import org.springframework.web.bind.annotation.*;
import ru.panic.template.dto.GameDiceRequestDto;
import ru.panic.template.dto.GameDiceResponseDto;
import ru.panic.template.service.impl.GameDiceServiceImpl;

@RestController
@RequestMapping("/api/v1")
public class GameDiceController {
    public GameDiceController(GameDiceServiceImpl gameDiceService) {
        this.gameDiceService = gameDiceService;
    }

    private final GameDiceServiceImpl gameDiceService;
    @PostMapping("/playDice")
    private GameDiceResponseDto playDice(
            @RequestHeader String jwtToken,
            @RequestBody GameDiceRequestDto request
    ){
        return gameDiceService.handleDice(jwtToken, request);
    }
}
