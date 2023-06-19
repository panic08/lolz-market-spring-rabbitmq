package ru.panic.template.repository.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.panic.generatedClasses.tables.Games;
import ru.panic.template.entity.Game;
import ru.panic.template.repository.GameRepository;

import java.util.List;

@Repository
public class GameRepositoryImpl implements GameRepository {
    public GameRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    private final DSLContext dslContext;
    @Override
    public List<Game> findByUserId(long id) {
        return dslContext.selectFrom(Games.GAMES).where(Games.GAMES.USER_ID.eq(id)).fetchInto(Game.class);
    }
    @Override
    public void save(Game game) {
        dslContext.insertInto(Games.GAMES)
                .set(Games.GAMES.USER_ID, game.getUserId())
                .set(Games.GAMES.USERNAME, game.getUsername())
                .set(Games.GAMES.GAME, game.getGame().toString())
                .set(Games.GAMES.GAME_STATE, game.getGameState().toString())
                .set(Games.GAMES.AMOUNT, game.getAmount())
                .set(Games.GAMES.CURRENCY, game.getCurrency().toString())
                .set(Games.GAMES.TIMESTAMP, game.getTimestamp())
                .execute();
    }
}
