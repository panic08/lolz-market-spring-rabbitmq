package ru.panic.template.dto;

import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.Currency;

@Data
public class ReplenishmentRequestDto {
    private Number amount;
    private Currency currency;
    private CryptoCurrency cryptoCurrency;
}
