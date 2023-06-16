package ru.panic.template.repository;

import ru.panic.template.entity.Withdrawal;
import ru.panic.template.enums.Status;

import java.util.List;

public interface WithdrawalRepository {
    List<Withdrawal> findAllByUserId(long userId);
    void removeById(long id);
    Withdrawal findById(long id);
    void updateStatusById(long id, Status status);
}
