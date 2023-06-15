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
    private static final String COIN_PROVIDER_URL = "https://api.coingecko.com/api/v3/simple/price?ids=tron,bitcoin,ethereum,litecoin,ripple,matic-network,tether&vs_currencies=rub,eur,usd,pln";
    @Override
    public ReplenishmentResponseDto payByCrypto(String jwtToken, ReplenishmentRequestDto request) {
        ResponseEntity<UserResponseDto> response = restTemplate.exchange(AUTH_URL + jwtToken, HttpMethod.POST, null, UserResponseDto.class);

        if(response.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        switch (request.getCurrency()){
            case RUB -> {
                if(request.getAmount().doubleValue() < 10){
                    throw new InvalidCredentialsException("Минимальная сумма для пополнения российским рублем - 10 RUB");
                }
            }
            case USD -> {
                if(request.getAmount().doubleValue() < 0.15){
                    throw new InvalidCredentialsException("Минимальная сумма для пополнения долларом США - 0.15$");
                }
            }
            case EUR -> {
                if(request.getAmount().doubleValue() < 0.15){
                    throw new InvalidCredentialsException("Минимальная сумма для пополнения европейским евро - 0.15€");
                }
            }
            case PLN -> {
                if(request.getAmount().doubleValue() < 0.50){
                    throw new InvalidCredentialsException("Минимальная сумма для пополнения польским злотым - 0.35 PLN");
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
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getRub(), 1e8);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getEur(), 1e8);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getUsd(), 1e8);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getBitcoin().getPln(), 1e8);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getBtcWallet());
            }
            case ETH -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getEthWallet());
            }
            case LTC -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getLtcWallet());
            }
            case TETHER_ERC20 -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTether().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getTetherERC20Wallet());
            }
            case TRX -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getTron().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getTrxWallet());
            }
            case MATIC -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getMaticNetwork().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getMaticWallet());
            }
            case XRP -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getRub(), 1e6);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getEur(), 1e6);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getUsd(), 1e6);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getPln(), 1e6);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getXrpWallet());
            }
        }

        ReplenishmentServiceHash replenishmentServiceHash = new ReplenishmentServiceHash();
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
                                    .getForEntity("https://blockchain.info/rawaddr/" + networkRabbit.getBtcWallet() + "?limit=3", BitcoinResponseDto.class);
                            if(bitcoinResponseDto
                                    .getBody()
                                    .getTxs()
                                    .get(0)
                                    .getInputs()
                                    .get(0).getPrev_out().getValue()/1e8 == replenishmentResponseDto.getAmount()
                                    &&
                                    bitcoinResponseDto.getBody().getTxs().get(0).getTime() > currentTime
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
                            if ((double) Long.parseLong(
                                    ethereumResponseDto.getBody().getResult().get(0).getValue()
                            )/ 1e18 == replenishmentResponseDto.getAmount()
                            &&
                                    Long.parseLong(ethereumResponseDto.getBody().getResult().get(0).getTimeStamp()) > currentTime
                            ) {
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
                                            + networkRabbit.getLtcWallet() + "?pageSize=3&sort=desc", LitecoinResponseDto.class);
                            if(Double.parseDouble(liteCoinResponseDto
                                    .getBody().getReplenishments()
                                    .get(0)
                                    .getInputs()
                                    .get(0)
                                    .getCoin()
                                    .getValue()) == replenishmentResponseDto.getAmount()
                                    &&
                                    liteCoinResponseDto.getBody().getReplenishments().get(0).getTime() > currentTime
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
                                            + networkRabbit.getMaticWallet() + "?pageSize=3&sort=desc", MaticResponseDto.class);
                            if ((double) Long.parseLong(
                                    maticResponseDto.getBody().getResponseDtos().get(0).getValue()
                            )/ 1e18 == replenishmentResponseDto.getAmount()
                                    &&
                                    maticResponseDto.getBody().getResponseDtos().get(0).getTimestamp() > currentTime
                            ) {
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
                                    .getForEntity("https://api.trongrid.io/v1/accounts/" + networkRabbit.getTrxWallet() + "/transactions?limit=3", TronResponseDto.class);
                            if(tronResponseDto.getBody()
                                    .getData()[0]
                                    .getRaw_data()
                                    .getContract()[0]
                                    .getParameter()
                                    .getValue()
                                    .getAmount()/1e6 == replenishmentResponseDto.getAmount()
                                    &&
                                    tronResponseDto.getBody().getData()[0].getRaw_data().getTimestamp() > currentTime
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
                        XrpAccountTxRequest request = new XrpAccountTxRequest(networkRabbit.getXrpWallet(), 3);
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
                            if(Double.parseDouble(xrpResponseDto.getBody().getResult().getTransactions().get(0).getTx().getAmount())/1e6
                                    ==
                                    replenishmentResponseDto.getAmount()
                                    &&
                                    xrpResponseDto.getBody().getResult().getTransactions().get(0).getTx().getDate()+946684800L>currentTime
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
                            if(Double.parseDouble(tetherERC20ResponseDto.getBody().getResult().get(0).getValue())
                                    /
                                    (10^Integer.parseInt(tetherERC20ResponseDto.getBody().getResult().get(0).getTokenDecimal()))
                                    ==
                                    replenishmentResponseDto.getAmount()
                                    &&
                                    Long.parseLong(tetherERC20ResponseDto.getBody().getResult().get(0).getTimeStamp())>currentTime
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
