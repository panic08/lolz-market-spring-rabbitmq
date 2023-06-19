package ru.panic.template.repository;

import ru.panic.template.dto.UserResponseDto;
import ru.panic.template.enums.Rank;

public interface UserRepository {
    void updateBtcBalanceById(long id, double balance);
    void updateEthBalanceById(long id, double balance);
    void updateLtcBalanceById(long id, double balance);
    void updateTonBalanceById(long id, double balance);
    void updateTrxBalanceById(long id, double balance);
    void updateXrpBalanceById(long id, double balance);
    void updateMaticBalanceById(long id, double balance);
    void updateTetherERC20BalanceById(long id, double balance);
    void updateProgressById(long id, double progress);
    void updateRankById(long id, Rank rank);
    UserResponseDto findById(long id);

}
