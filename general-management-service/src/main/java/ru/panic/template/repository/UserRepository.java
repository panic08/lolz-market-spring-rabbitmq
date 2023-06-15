package ru.panic.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panic.template.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
