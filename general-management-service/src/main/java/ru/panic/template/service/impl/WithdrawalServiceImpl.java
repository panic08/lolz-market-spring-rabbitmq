package ru.panic.template.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.entity.User;
import ru.panic.template.entity.Withdrawal;
import ru.panic.template.enums.Status;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.repository.WithdrawalRepository;
import ru.panic.template.service.WithdrawalService;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {
    public WithdrawalServiceImpl(WithdrawalRepository withdrawalRepository, UserRepository userRepository) {
        this.withdrawalRepository = withdrawalRepository;
        this.userRepository = userRepository;
    }
    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;
    @Transactional
    @Override
    public void handleWithdrawal(WithdrawalRequestDto request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        switch (request.getCurrency()){
            case BTC -> {
                if (user.getData().getBtcBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setBtcBalance(user.getData().getBtcBalance() - (request.getAmount()+request.getGas()));
            }
            case ETH -> {
                if (user.getData().getEthBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setEthBalance(user.getData().getEthBalance() - (request.getAmount()+request.getGas()));
            }
            case LTC -> {
                if (user.getData().getLtcBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setLtcBalance(user.getData().getLtcBalance() - (request.getAmount()+request.getGas()));
            }
            case TRX -> {
                if (user.getData().getTrxBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setTrxBalance(user.getData().getTrxBalance() - (request.getAmount()+request.getGas()));
            }
            case TON -> {
                if (user.getData().getTonBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setTonBalance(user.getData().getTonBalance() - (request.getAmount()+request.getGas()));
            }
            case XRP -> {
                if (user.getData().getXrpBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setXrpBalance(user.getData().getXrpBalance() - (request.getAmount()+request.getGas()));
            }
            case MATIC -> {
                if (user.getData().getMaticBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setMaticBalance(user.getData().getMaticBalance() - (request.getAmount()+request.getGas()));
            }
            case TETHER_ERC20 -> {
                if (user.getData().getMaticBalance() < (request.getAmount()+request.getGas())){
                    return;
                }

                user.getData().setTetherERC20Balance(user.getData().getTetherERC20Balance() - (request.getAmount()+request.getGas()));
            }
        }

        userRepository.save(user);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(request.getUserId());
        withdrawal.setUsername(request.getUsername());
        withdrawal.setStatus(Status.PENDING);
        withdrawal.setWalletId(request.getWalletId());
        withdrawal.setCurrency(request.getCurrency());
        withdrawal.setAmount(request.getAmount());
        withdrawal.setTimestamp(request.getTimestamp());

        withdrawalRepository.save(withdrawal);

    }
}
