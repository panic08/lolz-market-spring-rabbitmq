package ru.panic.template.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.panic.template.dto.GameDiceRequestDto;
import ru.panic.template.dto.GameDiceResponseDto;
import ru.panic.template.dto.UserResponseDto;
import ru.panic.template.entity.Game;
import ru.panic.template.enums.GameState;
import ru.panic.template.enums.GameType;
import ru.panic.template.enums.Rank;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.impl.GameRepositoryImpl;
import ru.panic.template.repository.impl.UserRepositoryImpl;
import ru.panic.template.service.GameDiceService;
import ru.panic.util.DecimalPlaces;

import java.util.List;
import java.util.Random;

@Service
public class GameDiceServiceImpl implements GameDiceService {
    public GameDiceServiceImpl(RestTemplate restTemplate, GameRepositoryImpl gameRepository, UserRepositoryImpl userRepository) {
        this.restTemplate = restTemplate;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    private final RestTemplate restTemplate;
    private final GameRepositoryImpl gameRepository;
    private final UserRepositoryImpl userRepository;
    private static Random random;
    @Override
    public GameDiceResponseDto handleDice(String jwtToken, GameDiceRequestDto request) {
        ResponseEntity<UserResponseDto> userResponseDtoResponseEntity =
                restTemplate.getForEntity("http://localhost:8080/api/v1/getInfoByJwt?jwtToken=" + jwtToken, UserResponseDto.class);

        if (userResponseDtoResponseEntity.getStatusCode().isError()){
            throw new InvalidCredentialsException("Неверный JWT токен");
        }

        if (request.getPercent()>95){
            throw new InvalidCredentialsException("Максимальный процент для ставки - 95");
        }
        if (request.getPercent()<2){
            throw new InvalidCredentialsException("Минимальный процент для ставки - 2");
        }

        switch (request.getCurrency()){
            case BTC -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 6){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке BTC должно быть небольше 6");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getBtcBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateBtcBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getBtcBalance()-request.getAmount(), 6));
            }

            case ETH -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 5){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке ETH должно быть небольше 5");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getEthBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateEthBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getEthBalance()-request.getAmount(), 5));
            }

            case LTC -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 4){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке ETH должно быть небольше 4");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getLtcBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateLtcBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getLtcBalance()-request.getAmount(), 4));
            }

            case TON -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 2){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке TON должно быть небольше 2");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getTonBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateTonBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getTonBalance()-request.getAmount(), 2));
            }
            case TRX -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 2){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке TRX должно быть небольше 2");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getTrxBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateTrxBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getTrxBalance()-request.getAmount(), 2));
            }
            case XRP -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 2){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке XRP должно быть небольше 2");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getXrpBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateXrpBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getXrpBalance()-request.getAmount(), 2));
            }
            case MATIC -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 2){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке MATIC должно быть небольше 2");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getMaticBalance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateMaticBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getMaticBalance()-request.getAmount(), 2));
            }
            case TETHER_ERC20 -> {
                if (DecimalPlaces.countDecimalPlaces(request.getAmount()) > 2){
                    throw new InvalidCredentialsException("Чисел после запятой при ставке TETHER-ERC20 должно быть небольше 2");
                }
                if (userResponseDtoResponseEntity.getBody().getData().getTetherERC20Balance() < request.getAmount()){
                    throw new InvalidCredentialsException("У вас не хватает средств");
                }
                userRepository.updateTetherERC20BalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        DecimalPlaces.round(userResponseDtoResponseEntity.getBody().getData().getTetherERC20Balance()-request.getAmount(), 2));
            }
        }

        List<Game> allGamesByUserId = gameRepository.findByUserId(userResponseDtoResponseEntity.getBody().getId());

        int winState = 0;
        int loseState = 0;

        for(Game key : allGamesByUserId){
            if (key.getGameState() == GameState.WIN){
                winState++;
            }else{
                if (key.getGameState() == GameState.LOSE){
                    loseState++;
                }
            }
        }

        boolean isWin = false;

        int randomNumber = random.nextInt(100) + 1;
        randomNumber = winState > loseState ? randomNumber + 10 : randomNumber;

        if (request.getPercent() > randomNumber){
            isWin = true;
            switch (request.getCurrency()){
                case BTC -> userRepository.updateBtcBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getBtcBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 6)
                );
                case ETH -> userRepository.updateEthBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getEthBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 5)
                );
                case LTC -> userRepository.updateLtcBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getLtcBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 4)
                );
                case TRX -> userRepository.updateTrxBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getTrxBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 2)
                );
                case TON -> userRepository.updateTonBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getTonBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 2)
                );
                case XRP -> userRepository.updateXrpBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getXrpBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 2)
                );
                case MATIC -> userRepository.updateMaticBalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getMaticBalance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 2)
                );
                case TETHER_ERC20 -> userRepository.updateTetherERC20BalanceById(userResponseDtoResponseEntity.getBody().getId(),
                        userResponseDtoResponseEntity.getBody().getData().getTetherERC20Balance() +
                                DecimalPlaces.round((double) 100 / request.getPercent(), 2)
                );
            }
        }

        Game game = new Game();
        game.setGame(GameType.DICE);
        game.setGameState(isWin ? GameState.WIN : GameState.LOSE);
        game.setAmount(request.getAmount());
        game.setCurrency(request.getCurrency());
        game.setUserId(userResponseDtoResponseEntity.getBody().getId());
        game.setUsername(userResponseDtoResponseEntity.getBody().getUsername());
        game.setTimestamp(System.currentTimeMillis());

        gameRepository.save(game);

        if (userResponseDtoResponseEntity.getBody().getData().getLevel().getProgress() == 99 &&
            userResponseDtoResponseEntity.getBody().getData().getLevel().getRank() != Rank.ELITE
        ){
            switch (userResponseDtoResponseEntity.getBody().getData().getLevel().getRank()){
                case BRONZE -> {
                    userRepository.updateRankById(userResponseDtoResponseEntity.getBody().getId(), Rank.SILVER);
                    userRepository.updateProgressById(userResponseDtoResponseEntity.getBody().getId(), 0);
                }
                case SILVER -> {
                    userRepository.updateRankById(userResponseDtoResponseEntity.getBody().getId(), Rank.GOLD);
                    userRepository.updateProgressById(userResponseDtoResponseEntity.getBody().getId(), 0);
                }
                case GOLD -> {
                    userRepository.updateRankById(userResponseDtoResponseEntity.getBody().getId(), Rank.PLATINUM);
                    userRepository.updateProgressById(userResponseDtoResponseEntity.getBody().getId(), 0);
                }
                case PLATINUM -> {
                    userRepository.updateRankById(userResponseDtoResponseEntity.getBody().getId(), Rank.DIAMOND);
                    userRepository.updateProgressById(userResponseDtoResponseEntity.getBody().getId(), 0);
                }
                case DIAMOND -> {
                    userRepository.updateRankById(userResponseDtoResponseEntity.getBody().getId(), Rank.ELITE);
                    userRepository.updateProgressById(userResponseDtoResponseEntity.getBody().getId(), 0);
                }
            }
        }else{
            if (userResponseDtoResponseEntity.getBody().getData().getLevel().getProgress() != 99 &&
                userResponseDtoResponseEntity.getBody().getData().getLevel().getRank() != Rank.ELITE
            ){
                userRepository.updateProgressById(userResponseDtoResponseEntity.getBody().getId(), 1.5);
            }
        }

        GameDiceResponseDto response = new GameDiceResponseDto();
        response.setGame(GameType.DICE);
        response.setGameState(isWin ? GameState.WIN : GameState.LOSE);
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setUserId(userResponseDtoResponseEntity.getBody().getId());
        response.setUsername(userResponseDtoResponseEntity.getBody().getUsername());
        response.setTimestamp(game.getTimestamp());
        return response;
    }
}
