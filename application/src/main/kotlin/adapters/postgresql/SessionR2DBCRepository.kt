package adapters.postgresql

import Session
import UserIdentifier
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import repositories.SessionRepository

@Component
class SessionR2DBCRepository(val databaseClient: DatabaseClient) : SessionRepository {
    override fun findTodaySessionForUser(userIdentifier: UserIdentifier): Session? {
        TODO("Not yet implemented")
    }

    override fun save(session: Session) {
        databaseClient
            .sql {
                """
            INSERT INTO sessions (user_identifier, session_started_date, question_id) 
            VALUES (:user_identifier, session_started_date, question_id)
        """.trimIndent()
            }
            .bind("user_identifier", session.userIdentifier.userIp)
            .bind("session_started_date", session.sessionStartedDate.date.toEpochDay())
            .bind("question_id", session.question.questionId.id.toString())
    }
}
