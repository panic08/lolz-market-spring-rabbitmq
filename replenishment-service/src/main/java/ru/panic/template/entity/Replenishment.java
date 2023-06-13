package ru.panic.template.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import ru.panic.template.entity.enums.CryptoCurrency;
@Entity
@Table(name = "replenishments")
@Data
public class Replenishment {
    @Id
    private Long id;
    private String username;
    private String walletId;
    private Double amount;
    private CryptoCurrency currency;
    private Long timestamp;
}
