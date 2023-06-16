package ru.panic.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.panic.template.entity.Replenishment;
@Repository
public interface ReplenishmentRepository extends JpaRepository<Replenishment, Long> {
}
