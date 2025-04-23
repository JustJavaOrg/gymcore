CREATE TABLE IF NOT EXISTS classes (
                                       id          SERIAL PRIMARY KEY,
                                       title       VARCHAR(255) NOT NULL,
                                       description TEXT,
                                       start_time  TIMESTAMP NOT NULL,
                                       end_time    TIMESTAMP NOT NULL,
                                       capacity    INTEGER NOT NULL,
                                       trainer_id  INTEGER NOT NULL,
                                       CONSTRAINT fk_trainer
                                           FOREIGN KEY (trainer_id) REFERENCES users(id)
);
