package ru.panic.template.repository;

public interface UserRepository {
    void updateBtcBalanceById(long id, Double amount);
    void updateEthBalanceById(long id, Double amount);
    void updateLtcBalanceById(long id, Double amount);
    void updateTonBalanceById(long id, Double amount);
    void updateTrxBalanceById(long id, Double amount);
    void updateXrpBalanceById(long id, Double amount);
    void updateMaticBalanceById(long id, Double amount);
    void updateTetherERC20BalanceById(long id, Double amount);


}
