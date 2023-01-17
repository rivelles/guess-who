CREATE TABLE IF NOT EXISTS questions (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(40) UNIQUE NOT NULL,
    description VARCHAR(300) NOT NULL,
    answer VARCHAR(300) NOT NULL,
    image VARCHAR(300) NOT NULL,
    date_appearance TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessions (
                                        id INT PRIMARY KEY,
                                        external_id VARCHAR(40) UNIQUE NOT NULL,
    question_id INT NOT NULL,
    user_identifier VARCHAR(200) NOT NULL,
    session_started_date TIMESTAMP,
    CONSTRAINT fk_question
    FOREIGN KEY (question_id) REFERENCES questions (id)
    );

CREATE TABLE IF NOT EXISTS question_tips (
    id SERIAL PRIMARY KEY,
    external_id VARCHAR(40) UNIQUE NOT NULL,
    question_id INT NOT NULL,
    tip VARCHAR(400) NOT NULL,
    CONSTRAINT fk_question
    FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE IF NOT EXISTS showed_tips (
    session_id INT,
    tip_id INT,
    CONSTRAINT fk_session
    FOREIGN KEY (session_id) REFERENCES sessions (id),
    CONSTRAINT fk_tip
    FOREIGN KEY (tip_id) REFERENCES question_tips (id)
);