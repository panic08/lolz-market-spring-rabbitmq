package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.entity.enums.CryptoCurrency;
import ru.panic.template.entity.enums.Currency;

@Data
public class ReplenishmentRequestDto {
    private Number amount;
    private Currency currency;
    private CryptoCurrency cryptoCurrency;
}
