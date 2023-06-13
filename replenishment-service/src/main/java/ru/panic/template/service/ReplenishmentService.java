package ru.panic.template.service;

import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.dto.ReplenishmentResponseDto;
import ru.panic.template.service.hash.ReplenishmentServiceHash;

import java.util.List;

public interface ReplenishmentService {
    ReplenishmentResponseDto payByCrypto(String jwtToken, ReplenishmentRequestDto request);
    List<ReplenishmentServiceHash> getAllUnsuccessfulReplenishments(String jwtToken);
}
