package ru.panic.template.service;

import ru.panic.template.dto.ReplenishmentHashRequestDto;

public interface ReplenishmentService {
    void handleReplenishment(ReplenishmentHashRequestDto request);
}
