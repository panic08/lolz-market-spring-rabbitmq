package ru.panic.template.repository;

import org.springframework.data.repository.CrudRepository;
import ru.panic.template.service.hash.ReplenishmentServiceHash;

public interface ReplenishmentServiceHashRepository extends CrudRepository<ReplenishmentServiceHash, String> {
}
