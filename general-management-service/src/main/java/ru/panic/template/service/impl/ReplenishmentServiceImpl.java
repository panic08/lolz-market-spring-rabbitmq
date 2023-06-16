package ru.panic.template.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.panic.template.dto.ReplenishmentHashRequestDto;
import ru.panic.template.entity.Replenishment;
import ru.panic.template.entity.User;
import ru.panic.template.repository.ReplenishmentRepository;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.service.ReplenishmentService;

@Service
public class ReplenishmentServiceImpl implements ReplenishmentService {
    public ReplenishmentServiceImpl(ReplenishmentRepository replenishmentRepository, UserRepository userRepository) {
        this.replenishmentRepository = replenishmentRepository;
        this.userRepository = userRepository;
    }
    private final ReplenishmentRepository replenishmentRepository;
    private final UserRepository userRepository;
    @Transactional
    @Override
    public void handleReplenishment(ReplenishmentHashRequestDto request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        switch (request.getCryptoCurrency()){
            case BTC -> user.getData().setBtcBalance(user.getData().getBtcBalance()+request.getAmount());

            case ETH -> user.getData().setEthBalance(user.getData().getEthBalance()+request.getAmount());

            case LTC -> user.getData().setLtcBalance(user.getData().getLtcBalance()+request.getAmount());

            case TRX -> user.getData().setTrxBalance(user.getData().getTrxBalance()+request.getAmount());

            case TON -> user.getData().setTonBalance(user.getData().getTonBalance()+request.getAmount());

            case XRP -> user.getData().setXrpBalance(user.getData().getXrpBalance()+request.getAmount());

            case MATIC -> user.getData().setMaticBalance(user.getData().getMaticBalance()+request.getAmount());

            case TETHER_ERC20 -> user.getData().setTetherERC20Balance(user.getData().getTetherERC20Balance()+request.getAmount());
        }

        userRepository.save(user);

        Replenishment replenishment = new Replenishment();
        replenishment.setUserId(request.getUserId());
        replenishment.setUsername(request.getUsername());
        replenishment.setAmount(request.getAmount());
        replenishment.setCurrency(request.getCryptoCurrency());
        replenishment.setTimestamp(request.getTimestamp());

        replenishmentRepository.save(replenishment);
    }
}
