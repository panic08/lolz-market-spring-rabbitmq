package ru.panic.template.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.CoinProviderResponseDto;
import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.dto.ReplenishmentResponseDto;
import ru.panic.template.dto.UserResponseDto;
import ru.panic.template.dto.crypto.*;
import ru.panic.template.dto.crypto.xrp.XrpAccountTxRequest;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.rabbit.NetworkRabbit;
import ru.panic.template.repository.ReplenishmentServiceHashRepository;
import ru.panic.template.service.ReplenishmentService;
import ru.panic.template.service.hash.ReplenishmentServiceHash;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class ReplenishmentServiceImpl implements ReplenishmentService {
    public ReplenishmentServiceImpl(NetworkRabbit networkRabbit, RestTemplate restTemplate, ReplenishmentServiceHashRepository replenishmentServiceHashRepository, RabbitTemplate rabbitTemplate) {
        this.networkRabbit = networkRabbit;
        this.restTemplate = restTemplate;
        this.replenishmentServiceHashRepository = replenishmentServiceHashRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private final NetworkRabbit networkRabbit;
    private final RestTemplate restTemplate;
    private final ReplenishmentServiceHashRepository replenishmentServiceHashRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final String AUTH_URL = "http://localhost:8080/api/v1/getInfoByJwt?jwtToken=";
    private static final String COIN_PROVIDER_URL =
            "https://api.coingecko.com/api/v3/simple/price?ids=tron,bitcoin,ethereum,litecoin,ripple,matic-network,tether,the-open-network&vs_currencies=rub,eur,usd,pln";
    @Override
    public ReplenishmentResponseDto payByCrypto(String jwtToken, ReplenishmentRequestDto request) {
        ResponseEntity<UserResponseDto> response = restTemplate.exchange(AUTH_URL + jwtToken, HttpMethod.POST, null, UserResponseDto.class);

        if(response.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        switch (request.getCryptoCurrency()) {
            case BTC, ETH, TETHER_ERC20, LTC -> {
                switch (request.getCurrency()) {
                    case RUB -> {
                        if (request.getAmount().doubleValue() < 500) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения российским рублем - 500 RUB");
                        }
                    }
                    case USD -> {
                        if (request.getAmount().doubleValue() < 6) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения долларом США - 6.0$");
                        }
                    }
                    case EUR -> {
                        if (request.getAmount().doubleValue() < 5.5) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения европейским евро - 5.5€");
                        }
                    }
                    case PLN -> {
                        if (request.getAmount().doubleValue() < 24.5) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения польским злотым - 24.5 PLN");
                        }
                    }
                }
            }

            case TON, TRX, XRP, MATIC -> {
                switch (request.getCurrency()) {
                    case RUB -> {
                        if (request.getAmount().doubleValue() < 90) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения российским рублем - 90 RUB");
                        }
                    }
                    case USD -> {
                        if (request.getAmount().doubleValue() < 1) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения долларом США - 1.0$");
                        }
                    }
                    case EUR -> {
                        if (request.getAmount().doubleValue() < 0.95) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения европейским евро - 0.95€");
                        }
                    }
                    case PLN -> {
                        if (request.getAmount().doubleValue() < 4) {
                            throw new InvalidCredentialsException("Минимальная сумма для пополнения польским злотым - 4.0 PLN");
                        }
                    }
                }
            }
        }

        ResponseEntity<CoinProviderResponseDto> coinProviderResponseDto =
                restTemplate.getForEntity(COIN_PROVIDER_URL, CoinProviderResponseDto.class);

        ReplenishmentResponseDto replenishmentResponseDto = new ReplenishmentResponseDto();
        replenishmentResponseDto.setStatus(200);
        replenishmentResponseDto.setCurrency(request.getCryptoCurrency());
        replenishmentResponseDto.setTimestamp(System.currentTimeMillis());

        CoinProviderResponseDto crypto = coinProviderResponseDto.getBody();
        switch (request.getCryptoCurrency()) {
            case BTC -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getBtcWallet());
            }
            case ETH -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getRub(), 1e5);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getEur(), 1e5);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getUsd(), 1e5);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getPln(), 1e5);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getEthWallet());
            }
            case LTC -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getRub(), 1e4);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getEur(), 1e4);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getUsd(), 1e4);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getPln(), 1e4);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getLtcWallet());
            }
            case TETHER_ERC20 -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getRub(), 1e2);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getEur(), 1e2);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getUsd(), 1e2);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getPln(), 1e2);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getTetherERC20Wallet());
            }
            case TRX -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getRub(), 1e2);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getEur(), 1e2);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getUsd(), 1e2);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getPln(), 1e2);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getTrxWallet());
            }
            case MATIC -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getRub(), 1e2);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getEur(), 1e2);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getUsd(), 1e2);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getPln(), 1e2);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getMaticWallet());
            }
            case XRP -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getRub(), 1e2);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getEur(), 1e2);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getUsd(), 1e2);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getPln(), 1e2);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getXrpWallet());
            }
            case TON -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTheOpenNetwork().getRub(), 1e2);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTheOpenNetwork().getEur(), 1e2);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTheOpenNetwork().getUsd(), 1e2);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTheOpenNetwork().getPln(), 1e2);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getTonWallet());
            }
        }

        ReplenishmentServiceHash replenishmentServiceHash = new ReplenishmentServiceHash();
        replenishmentServiceHash.setUserId(response.getBody().getId());
        replenishmentServiceHash.setUsername(response.getBody().getUsername());
        replenishmentServiceHash.setCryptoCurrency(request.getCryptoCurrency());
        replenishmentServiceHash.setAmount(replenishmentResponseDto.getAmount());
        replenishmentServiceHash.setTimestamp(System.currentTimeMillis());

        replenishmentServiceHashRepository.save(replenishmentServiceHash);

        new Thread(() -> {
            switch (request.getCryptoCurrency()){
                case BTC -> {
                    long currentTime = System.currentTimeMillis();
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {
                            ResponseEntity<BitcoinResponseDto> bitcoinResponseDto = restTemplate
                                    .getForEntity("https://blockchain.info/rawaddr/" + networkRabbit.getBtcWallet() + "?limit=5", BitcoinResponseDto.class);

                            for (BitcoinResponseDto.TransactionDTO tx : bitcoinResponseDto.getBody().getTxs()) {
                                for (BitcoinResponseDto.InputDTO input : tx.getInputs()) {
                                    if(input.getPrev_out().getValue()/1e8 == replenishmentResponseDto.getAmount()
                                            && tx.getTime() > currentTime
                                    ){
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        String jsonString;
                                        try {
                                            jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }
                                        rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                        replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                        return;
                                    }
                                }
                            }

                            counter++;
                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }

                case ETH -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {
                            ResponseEntity<EthereumResponseDto> ethereumResponseDto = restTemplate
                                    .getForEntity("https://api.etherscan.io/api?module=account&action=txlist&address="
                                            + networkRabbit.getEthWallet() + "&startblock=0&endblock=99999999&sort=desc&apikey="
                                            + networkRabbit.getEthApiToken() + "&offset=0&limit=5", EthereumResponseDto.class);

                            for (EthereumResponseDto.TransactionDto transactionDto : ethereumResponseDto.getBody().getResult()) {
                                if ((double) Long.parseLong(transactionDto.getValue())/1e18 == replenishmentResponseDto.getAmount()
                                        &&
                                        Long.parseLong(transactionDto.getTimeStamp()) > currentTime
                                ){
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    String jsonString;
                                    try {
                                        jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                    replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                    return;
                                }
                            }

                            counter++;
                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }

                case LTC -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {
                            ResponseEntity<LitecoinResponseDto> liteCoinResponseDto = restTemplate
                                    .getForEntity("https://api.tatum.io/v3/litecoin/transaction/address/"
                                            + networkRabbit.getLtcWallet() + "?pageSize=5&sort=desc", LitecoinResponseDto.class);

                            for (LitecoinResponseDto.ReplenishmentDto replenishment : liteCoinResponseDto.getBody().getReplenishments()) {
                                for (LitecoinResponseDto.ReplenishmentDto.InputDto input : replenishment.getInputs()) {
                                    if(Double.parseDouble(input.getCoin().getValue()) == replenishmentResponseDto.getAmount() && replenishment.getTime() > currentTime){
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        String jsonString;
                                        try {
                                            jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }
                                        rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                        replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                        return;
                                    }
                                }
                            }

                            counter++;

                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }
                case MATIC -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {
                            ResponseEntity<MaticResponseDto> maticResponseDto = restTemplate
                                    .getForEntity("https://api.tatum.io/v3/polygon/account/transaction/"
                                            + networkRabbit.getMaticWallet() + "?pageSize=5&sort=desc", MaticResponseDto.class);

                            for (MaticResponseDto.ResponseDto responseDto : maticResponseDto.getBody().getResponseDtos()) {
                                if ((double) Long.parseLong(responseDto.getValue())/1e18 == replenishmentResponseDto.getAmount()
                                        &&
                                        responseDto.getTimestamp() > currentTime
                                ){
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    String jsonString;
                                    try {
                                        jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                    replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                    return;
                                }
                            }

                            counter++;
                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }
                case TRX -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {
                            ResponseEntity<TronResponseDto> tronResponseDto = restTemplate
                                    .getForEntity("https://api.trongrid.io/v1/accounts/" + networkRabbit.getTrxWallet() + "/transactions?limit=5", TronResponseDto.class);

                            for (TronResponseDto.Data datum : tronResponseDto.getBody().getData()) {
                                for (TronResponseDto.Contract contract : datum.getRaw_data().getContract()) {
                                    if (contract.getParameter().getValue().getAmount()/1e6 == replenishmentResponseDto.getAmount() &&
                                            datum.getRaw_data().getTimestamp() > currentTime){
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        String jsonString;
                                        try {
                                            jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }
                                        rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                        replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                        return;
                                    }
                                }
                            }

                            counter++;

                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }
                case XRP -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        XrpAccountTxRequest request = new XrpAccountTxRequest(networkRabbit.getXrpWallet(), 5);
                        HttpHeaders headers = new HttpHeaders();
                        HttpEntity<XrpAccountTxRequest> entity = new HttpEntity<>(request, headers);
                        public void run() {

                            ResponseEntity<XrpResponseDto> xrpResponseDto = restTemplate
                                    .exchange(
                                            "https://s1.ripple.com:51234/",
                                            HttpMethod.POST,
                                            entity,
                                            XrpResponseDto.class
                                    );

                            for (XrpResponseDto.Transaction transaction : xrpResponseDto.getBody().getResult().getTransactions()) {
                                if(Double.parseDouble(transaction.getTx().getAmount()) == replenishmentResponseDto.getAmount()
                                        &&
                                        transaction.getTx().getDate()+946684800L > currentTime
                                ){
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    String jsonString;
                                    try {
                                        jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                    replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                    return;
                                }
                            }

                            counter++;

                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }

                case TETHER_ERC20 -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {
                            ResponseEntity<TetherERC20ResponseDto> tetherERC20ResponseDto = restTemplate
                                    .getForEntity("https://api.etherscan.io/api?module=account&action=tokentx&contractaddress=0xdac17f958d2ee523a2206206994597c13d831ec7&"
                                            + networkRabbit.getEthWallet() + "&" + networkRabbit.getEthApiToken() + "&page=1&offset=5&sort=desc", TetherERC20ResponseDto.class);

                            for (TetherERC20ResponseDto.ResultDTO resultDTO : tetherERC20ResponseDto.getBody().getResult()) {
                                if(Double.parseDouble(resultDTO.getValue())/(10^Integer.parseInt(resultDTO.getTokenDecimal())) == replenishmentResponseDto.getAmount()
                                        &&
                                        Long.parseLong(resultDTO.getTimeStamp()) > currentTime
                                ){
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    String jsonString;
                                    try {
                                        jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                    replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                    return;
                                }
                            }

                            counter++;

                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }

                case TON -> {
                    Timer timer = new Timer();
                    long currentTime = System.currentTimeMillis();
                    TimerTask task = new TimerTask() {
                        int counter = 0;
                        public void run() {

                            ResponseEntity<TonResponseDto> tonResponseDto = restTemplate
                                    .exchange(
                                            "https://stage.toncenter.com/api/v2/getTransactions?address=" + networkRabbit.getTonWallet() + "&limit=5&to_lt=0&archival=false",
                                            HttpMethod.GET,
                                            null,
                                            TonResponseDto.class
                                    );
                            for (TonResponseDto.ResultDTO resultDTO : tonResponseDto.getBody().getResult()) {
                                if((double) Long.parseLong(resultDTO.getIn_msg().getValue())/1e9
                                        ==
                                        replenishmentResponseDto.getAmount()
                                        &&
                                        resultDTO.getUtime() > currentTime
                                ){
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    String jsonString;
                                    try {
                                        jsonString = objectMapper.writeValueAsString(replenishmentServiceHash);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    rabbitTemplate.convertAndSend("replenishment-queue", jsonString);
                                    replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                    return;
                                }
                            }

                            counter++;

                            if (counter == 12 * 4) {
                                replenishmentServiceHashRepository.delete(replenishmentServiceHash);
                                timer.cancel(); // Отменяем таймер после 12 минут (12 * 4 = 48 повторений с интервалом в 3 секунды)
                            }
                        }
                    };

                    long delay = 0; // Задержка перед началом выполнения задачи
                    long period = 4000; // Интервал между повторениями задачи (4 секунды = 4000 миллисекунд)

                    timer.scheduleAtFixedRate(task, delay, period);
                }
            }
        }).start();

        return replenishmentResponseDto;
    }
    @Override
    public List<ReplenishmentServiceHash> getAllUnsuccessfulReplenishments(String jwtToken) {
        ResponseEntity<UserResponseDto> response = restTemplate.exchange(AUTH_URL + jwtToken, HttpMethod.POST, null, UserResponseDto.class);

        if(response.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        List<String> usernames = List.of(response.getBody().getUsername());
        List<ReplenishmentServiceHash> replenishmentServiceHashes = new ArrayList<>();
        replenishmentServiceHashRepository.findAllById(usernames).forEach(replenishmentServiceHashes::add);

        return replenishmentServiceHashes;
    }
    private void setRoundedAmount(ReplenishmentResponseDto replenishmentResponseDto, double amount, double zeros) {
        replenishmentResponseDto.setAmount(Math.ceil(amount * zeros) / zeros);
    }
}
