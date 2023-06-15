package ru.panic.template.service.hash;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.panic.template.entity.enums.CryptoCurrency;

@RedisHash("unsuccessfulReplenishments")
@Data
public class ReplenishmentServiceHash {
    @Id
    private String username;
    private Double amount;
    private CryptoCurrency cryptoCurrency;
    private Long timestamp;
}
