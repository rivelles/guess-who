CREATE TABLE IF NOT EXISTS questions (
     id VARCHAR(40) PRIMARY KEY,
     description VARCHAR(300) NOT NULL,
     answer VARCHAR(300) NOT NULL,
     image VARCHAR(300) NOT NULL,
     date_appearance TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(40) PRIMARY KEY,
    question_id VARCHAR(40) NOT NULL,
    user_identifier VARCHAR(200) NOT NULL,
    session_started_date TIMESTAMP,
    session_finished_date TIMESTAMP,
    CONSTRAINT fk_question
        FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE IF NOT EXISTS question_tips (
         id VARCHAR(40) PRIMARY KEY,
         question_id VARCHAR(40) NOT NULL,
         tip VARCHAR(400) NOT NULL,
         CONSTRAINT fk_question
             FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE IF NOT EXISTS showed_tips (
       session_id VARCHAR(40),
       tip_id VARCHAR(40)
);

ALTER TABLE showed_tips
    ADD CONSTRAINT session_fk
        FOREIGN KEY (session_id) REFERENCES sessions(id);

ALTER TABLE showed_tips
    ADD CONSTRAINT question_tips_fk
        FOREIGN KEY (tip_id) REFERENCES question_tips(id);