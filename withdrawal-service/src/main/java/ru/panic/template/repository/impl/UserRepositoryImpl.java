package ru.panic.template.repository.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.panic.generatedClasses.tables.Users;
import ru.panic.template.repository.UserRepository;

@Service
public class UserRepositoryImpl implements UserRepository {
    public UserRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    private final DSLContext dslContext;
    @Override
    public void updateBtcBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.BTC_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateEthBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.ETH_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateLtcBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.LTC_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateTonBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.TON_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateTrxBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.TRX_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateXrpBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.XRP_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateMaticBalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.MATIC_BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateTetherERC20BalanceById(long id, Double amount) {
        dslContext.update(Users.USERS).set(Users.USERS.TETHERERC20BALANCE, amount).where(Users.USERS.ID.eq(id)).execute();
    }
}
