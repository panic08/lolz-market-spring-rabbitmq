package ru.panic.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.panic.template.entity.Withdrawal;
@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
