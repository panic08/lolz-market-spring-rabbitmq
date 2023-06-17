package ru.panic.template.controller;

import org.springframework.web.bind.annotation.*;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalResponseDto;
import ru.panic.template.entity.Withdrawal;
import ru.panic.template.enums.Status;
import ru.panic.template.service.impl.WithdrawalServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WithdrawalController {
    public WithdrawalController(WithdrawalServiceImpl withdrawalService) {
        this.withdrawalService = withdrawalService;
    }
    private final WithdrawalServiceImpl withdrawalService;
    @PostMapping("/createWithdrawal")
    private WithdrawalResponseDto createWithdrawal(
            @RequestHeader String jwtToken,
            @RequestBody WithdrawalRequestDto request
    ){
        return withdrawalService.createWithdrawal(jwtToken, request);
    }
    @GetMapping("/getAllWithdrawalsById")
    private List<Withdrawal> getAllWithdrawalsById(
            @RequestHeader String jwtToken
    ){
        return withdrawalService.getAllWithdrawalsByUserId(jwtToken);
    }
    @DeleteMapping("/cancelWithdrawalById")
    private void cancelWithdrawalById(
            @RequestHeader String jwtToken,
            @RequestBody Withdrawal withdrawal
    ){
        withdrawalService.updateStatusById(jwtToken, withdrawal, Status.UNSUCCESSFULLY);
    }
}
