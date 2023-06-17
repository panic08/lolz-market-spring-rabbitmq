package ru.panic.template.controller;

import org.springframework.web.bind.annotation.*;
import ru.panic.template.dto.GameRequestDto;
import ru.panic.template.entity.GameResponseDto;

@RestController
@RequestMapping("/api/v1")
public class GameController {
    @PostMapping("/playDice")
    private GameResponseDto playDice(
            @RequestHeader String jwtToken,
            @RequestBody GameRequestDto request
            ){

        return null;
    }
}
