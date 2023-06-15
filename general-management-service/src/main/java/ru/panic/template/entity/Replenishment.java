package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;

@Entity
@Table(name = "replenishments")
@Data
public class Replenishment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "currency", nullable = false)
    private CryptoCurrency currency;
    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
}
