package ru.panic.template.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.dto.ReplenishmentResponseDto;
import ru.panic.template.service.hash.ReplenishmentServiceHash;
import ru.panic.template.service.impl.ReplenishmentServiceImpl;

import java.util.List;

@Controller
@RequestMapping("/api")
public class ReplenishmentController {
    public ReplenishmentController(ReplenishmentServiceImpl replenishmentService) {
        this.replenishmentService = replenishmentService;
    }
    private final ReplenishmentServiceImpl replenishmentService;
    @GetMapping("/getAllUnsuccessfulReplenishments")
    private List<ReplenishmentServiceHash> getAllUnsuccessfulReplenishments(@RequestHeader String jwtToken){
        return replenishmentService.getAllUnsuccessfulReplenishments(jwtToken);
    }
    @PostMapping("/payByCrypto")
    private ReplenishmentResponseDto payByCrypto(
            @RequestHeader String jwtToken,
            @RequestBody ReplenishmentRequestDto request
            ){
        return replenishmentService.payByCrypto(jwtToken, request);
    }
}
