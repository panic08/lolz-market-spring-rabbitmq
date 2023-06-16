package ru.panic.template.repository.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.panic.generatedClasses.tables.Withdrawals;
import ru.panic.template.entity.Withdrawal;
import ru.panic.template.enums.Status;
import ru.panic.template.repository.WithdrawalRepository;

import java.util.List;

@Repository
public class WithdrawalRepositoryImpl implements WithdrawalRepository {
    public WithdrawalRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    private final DSLContext dslContext;
    @Override
    public List<Withdrawal> findAllByUserId(long userId) {
        return dslContext.select().from(Withdrawals.WITHDRAWALS).where(Withdrawals.WITHDRAWALS.USER_ID.eq(userId)).fetch().into(Withdrawal.class);
    }
    @Override
    public void removeById(long id) {
        dslContext.deleteFrom(Withdrawals.WITHDRAWALS).where(Withdrawals.WITHDRAWALS.ID.eq(id)).execute();
    }

    @Override
    public Withdrawal findById(long id) {
        Withdrawal withdrawal = dslContext.selectFrom(Withdrawals.WITHDRAWALS).where(Withdrawals.WITHDRAWALS.ID.eq(id)).fetchOne().into(Withdrawal.class);
        return withdrawal != null ? withdrawal : null;
    }

    @Override
    public void updateStatusById(long id, Status status) {
        dslContext.update(Withdrawals.WITHDRAWALS).set(Withdrawals.WITHDRAWALS.STATUS, status.toString())
                .where(Withdrawals.WITHDRAWALS.ID.eq(id)).execute();
    }

}
