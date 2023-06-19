CREATE TABLE IF NOT EXISTS games (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    currency VARCHAR(255) NOT NULL,
    game VARCHAR(255) NOT NULL,
    game_state VARCHAR(255) NOT NULL,
    timestamp BIGINT NOT NULL
    );
