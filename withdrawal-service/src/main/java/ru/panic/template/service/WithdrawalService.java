package ru.panic.template.service;

import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalResponseDto;
import ru.panic.template.entity.Withdrawal;
import ru.panic.template.enums.Status;

import java.util.List;

public interface WithdrawalService {
    WithdrawalResponseDto createWithdrawal(String jwtToken, WithdrawalRequestDto request);
    List<Withdrawal> getAllWithdrawalsByUserId(String jwtToken);
    void removeWithdrawalById(String jwtToken, Withdrawal withdrawal);
    void updateStatusById(String jwtToken, Withdrawal withdrawal, Status status);
}
