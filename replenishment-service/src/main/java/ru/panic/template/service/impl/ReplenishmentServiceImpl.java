package ru.panic.template.service.impl;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.CoinProviderResponseDto;
import ru.panic.template.dto.ReplenishmentRequestDto;
import ru.panic.template.dto.ReplenishmentResponseDto;
import ru.panic.template.dto.UserResponseDto;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.rabbit.NetworkRabbit;
import ru.panic.template.repository.ReplenishmentServiceHashRepository;
import ru.panic.template.service.ReplenishmentService;
import ru.panic.template.service.hash.ReplenishmentServiceHash;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReplenishmentServiceImpl implements ReplenishmentService {
    public ReplenishmentServiceImpl(NetworkRabbit networkRabbit, RestTemplate restTemplate, ReplenishmentServiceHashRepository replenishmentServiceHashRepository) {
        this.networkRabbit = networkRabbit;
        this.restTemplate = restTemplate;
        this.replenishmentServiceHashRepository = replenishmentServiceHashRepository;
    }
    private final NetworkRabbit networkRabbit;
    private final RestTemplate restTemplate;
    private final ReplenishmentServiceHashRepository replenishmentServiceHashRepository;
    private static final String AUTH_URL = "http://localhost:8080/api/v1/getInfoByJwt?jwtToken=";
    private static final String COIN_PROVIDER_URL = "https://api.coingecko.com/api/v3/simple/price?ids=tron,bitcoin,ethereum&vs_currencies=rub,eur,usd,pln";
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
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getRub(), 1e8);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getEur(), 1e8);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getUsd(), 1e8);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getEthereum().getPln(), 1e8);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getEthWallet());
            }
            case LTC -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getRub(), 1e8);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getEur(), 1e8);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getUsd(), 1e8);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getLitecoin().getPln(), 1e8);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getLtcWallet());
            }
            case SOL -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getSolana().getRub(), 1e9);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getSolana().getEur(), 1e9);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getSolana().getUsd(), 1e9);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getSolana().getPln(), 1e9);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getSolWallet());
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
            case XRP -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getRub(), 1e8);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getEur(), 1e8);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getUsd(), 1e8);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getRipple().getPln(), 1e8);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getXrpWallet());
            }
            case DOGE -> {
                switch (request.getCurrency()) {
                    case RUB -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getDogecoin().getRub(), 1e8);
                    case EUR -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getDogecoin().getEur(), 1e8);
                    case USD -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getDogecoin().getUsd(), 1e8);
                    case PLN -> setRoundedAmount(replenishmentResponseDto, request.getAmount().doubleValue() / crypto.getDogecoin().getPln(), 1e8);
                }
                replenishmentResponseDto.setWalletId(networkRabbit.getDogeWallet());
            }
        }

        ReplenishmentServiceHash replenishmentServiceHash = new ReplenishmentServiceHash();
        replenishmentServiceHash.setUsername(response.getBody().getUsername());
        replenishmentServiceHash.setWalletId(request.getWalletId());
        replenishmentServiceHash.setCryptoCurrency(request.getCryptoCurrency());
        replenishmentServiceHash.setAmount(replenishmentResponseDto.getAmount());
        replenishmentServiceHash.setTimestamp(System.currentTimeMillis());

        replenishmentServiceHashRepository.save(replenishmentServiceHash);

        new Thread(() -> {

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
