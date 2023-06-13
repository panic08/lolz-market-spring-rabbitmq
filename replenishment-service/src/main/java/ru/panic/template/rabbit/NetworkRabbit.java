package ru.panic.template.rabbit;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NetworkRabbit {
    @Value("${ru.panic.networks.btc}")
    private String btcWallet;
    @Value("${ru.panic.networks.eth}")
    private String ethWallet;
    @Value("${ru.panic.networks.ltc}")
    private String ltcWallet;
    @Value("${ru.panic.networks.sol}")
    private String solWallet;
    @Value("${ru.panic.networks.trx}")
    private String trxWallet;
    @Value("${ru.panic.networks.xrp}")
    private String xrpWallet;
    @Value("${ru.panic.networks.doge}")
    private String dogeWallet;
}
