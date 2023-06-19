package ru.panic.template.repository.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.panic.generatedClasses.tables.Users;
import ru.panic.template.dto.UserResponseDto;
import ru.panic.template.enums.Rank;
import ru.panic.template.repository.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    public UserRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    private final DSLContext dslContext;


    @Override
    public void updateBtcBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.BTC_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateEthBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.ETH_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateLtcBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.LTC_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateTonBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.TON_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateTrxBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.TRX_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateXrpBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.XRP_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateMaticBalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.MATIC_BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateTetherERC20BalanceById(long id, double balance) {
        dslContext.update(Users.USERS).set(Users.USERS.TETHERERC20BALANCE, balance).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateProgressById(long id, double progress) {
        dslContext.update(Users.USERS).set(Users.USERS.PROGRESS, progress).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public void updateRankById(long id, Rank rank) {
        dslContext.update(Users.USERS).set(Users.USERS.RANK, rank.toString()).where(Users.USERS.ID.eq(id)).execute();
    }

    @Override
    public UserResponseDto findById(long id) {
        return dslContext.selectFrom(Users.USERS).where(Users.USERS.ID.eq(id)).fetchOneInto(UserResponseDto.class);
    }
}
