package ru.panic.template.entity;

import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.Status;
@Data
public class Withdrawal {
    private Long id;
    private Long userId;
    private String username;
    private String walletId;
    private Double amount;
    private Double gas;
    private Status status;
    private CryptoCurrency currency;
    private Long timestamp;
}
