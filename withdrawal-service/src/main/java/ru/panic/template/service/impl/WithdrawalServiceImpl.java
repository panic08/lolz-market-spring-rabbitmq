package ru.panic.template.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.UserResponseDto;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalResponseDto;
import ru.panic.template.entity.Withdrawal;
import ru.panic.template.enums.Status;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.impl.WithdrawalRepositoryImpl;
import ru.panic.template.service.WithdrawalService;

import java.util.List;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {
    public WithdrawalServiceImpl(RestTemplate restTemplate, RabbitTemplate rabbitTemplate, WithdrawalRepositoryImpl withdrawalRepository) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.withdrawalRepository = withdrawalRepository;
    }
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final WithdrawalRepositoryImpl withdrawalRepository;
    @Override
    public WithdrawalResponseDto createWithdrawal(String jwtToken, WithdrawalRequestDto request) {
        ResponseEntity<UserResponseDto> userResponseDto = restTemplate.getForEntity("http://localhost:8080/api/v1/getInfoByJwt?jwtToken=" + jwtToken, UserResponseDto.class);
        if(userResponseDto.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        double gas = request.getAmount() * 0.02;

        switch (request.getCurrency()){
            case BTC -> {
                if  (request.getAmount() < 0.000224){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 0.000224 BTC");
                }
                if (userResponseDto.getBody().getData().getBtcBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case ETH -> {
                if  (request.getAmount() < 0.00342){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 0.00342 ETH");
                }
                if (userResponseDto.getBody().getData().getEthBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case LTC -> {
                if  (request.getAmount() < 0.0772){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 0.0772 LTC");
                }
                if (userResponseDto.getBody().getData().getLtcBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case TRX -> {
                if  (request.getAmount() < 16.69){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 16.69 TRX");
                }
                if (userResponseDto.getBody().getData().getTrxBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case TON -> {
                if  (request.getAmount() < 0.837){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 0.837 TON");
                }
                if (userResponseDto.getBody().getData().getTonBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case XRP -> {
                if  (request.getAmount() < 2.475){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 2.475 XRP");
                }
                if (userResponseDto.getBody().getData().getXrpBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case MATIC -> {
                if  (request.getAmount() < 1.92){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 1.92 MATIC");
                }
                if (userResponseDto.getBody().getData().getMaticBalance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
            case TETHER_ERC20 -> {
                if  (request.getAmount() < 5.964){
                    throw new InvalidCredentialsException("Минимальная сумма для вывода - 5.964 TETHER ERC20");
                }
                if (userResponseDto.getBody().getData().getTetherERC20Balance() < (request.getAmount() + gas)){
                    throw new InvalidCredentialsException("Недостаточно средств для вывода");
                }
            }
        }

        WithdrawalResponseDto withdrawalResponseDto = new WithdrawalResponseDto();
        withdrawalResponseDto.setUserId(userResponseDto.getBody().getId());
        withdrawalResponseDto.setUsername(userResponseDto.getBody().getUsername());
        withdrawalResponseDto.setWalletId(request.getWalletId());
        withdrawalResponseDto.setAmount(request.getAmount());
        withdrawalResponseDto.setGas(gas);
        withdrawalResponseDto.setTimestamp(System.currentTimeMillis());
        withdrawalResponseDto.setCurrency(request.getCurrency());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = null;
        try {
            jsonRequest = objectMapper.writeValueAsString(withdrawalResponseDto);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        rabbitTemplate.convertAndSend("withdrawal-queue", jsonRequest);
        return withdrawalResponseDto;
    }

    @Override
    public List<Withdrawal> getAllWithdrawalsByUserId(String jwtToken) {
        ResponseEntity<UserResponseDto> userResponseDto = restTemplate.getForEntity("http://localhost:8080/api/v1/getInfoByJwt?jwtToken=" + jwtToken, UserResponseDto.class);
        if(userResponseDto.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        return withdrawalRepository.findAllByUserId(userResponseDto.getBody().getId());
    }
    @Override
    public void removeWithdrawalById(String jwtToken, Withdrawal withdrawal) {
        ResponseEntity<UserResponseDto> userResponseDto = restTemplate.getForEntity("http://localhost:8080/api/v1/getInfoByJwt?jwtToken=" + jwtToken, UserResponseDto.class);
        if(userResponseDto.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        Withdrawal withdrawal1 = withdrawalRepository.findById(withdrawal.getId());
        if (!withdrawal1.getUserId().equals(userResponseDto.getBody().getId())){
            throw new InvalidCredentialsException("Вы не можете отменить этот вывод");
        }

        withdrawalRepository.removeById(withdrawal.getId());
    }

    @Override
    public void updateStatusById(String jwtToken, Withdrawal withdrawal, Status status) {
        ResponseEntity<UserResponseDto> userResponseDto = restTemplate.getForEntity("http://localhost:8080/api/v1/getInfoByJwt?jwtToken=" + jwtToken, UserResponseDto.class);
        if(userResponseDto.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        if (!withdrawal.getUserId().equals(userResponseDto.getBody().getId())){
            throw new InvalidCredentialsException("Вы не можете отменить этот вывод");
        }

        withdrawalRepository.updateStatusById(withdrawal.getId(), status);
    }

}
