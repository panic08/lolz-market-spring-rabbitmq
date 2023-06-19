package ru.panic.template.repository;

import ru.panic.template.entity.Game;

import java.util.List;

public interface GameRepository {
    List<Game> findByUserId(long id);
    void save(Game game);
}
