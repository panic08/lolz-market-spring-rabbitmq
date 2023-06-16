package ru.panic.template.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.panic.template.enums.CryptoCurrency;
import ru.panic.template.enums.Status;

@Entity
@Table(name = "withdrawals")
@Data
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "wallet_id", nullable = false)
    private String walletId;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "gas", nullable = false)
    private Double gas;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoCurrency currency;
    @Column(name = "timestamp", nullable = false)
    private Long timestamp;
}
