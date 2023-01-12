package adapters.postgresql

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import Session
import SessionDate
import UserIdentifier
import java.time.LocalDate
import java.util.UUID
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import repositories.SessionRepository

@Component
class SessionR2DBCRepository(val databaseClient: DatabaseClient) : SessionRepository {
    override fun findTodaySessionForUser(userIdentifier: UserIdentifier): Session? {
        val today = LocalDate.now().toEpochDay()
        return databaseClient
            .sql {
                """
            SELECT * FROM sessions s
            INNER JOIN questions q ON s.question_id = q.id
            INNER JOIN showed_tips st ON s.id = st.session_id
            INNER JOIN question_tips qt ON q.id = qt.question_id
            WHERE user_identifier = :user_identifier 
            AND session_started_date = := session_started_date
        """.trimIndent()
            }
            .bind("user_identifier", userIdentifier.userIp)
            .bind("session_started_date", today)
            .map { row, _ ->
                val questionTips =
                    QuestionTips(
                        row.get("qt.tip", ArrayList<String>().javaClass)
                            ?: throw RuntimeException(
                                "Couldn't find question tips for user session"))
                val showedTips =
                    QuestionTips(
                        row.get("st.tip", ArrayList<String>().javaClass)
                            ?: throw RuntimeException("Couldn't find showed tips for user session"))
                val question =
                    Question(
                        questionId =
                            QuestionId(
                                row.get("q.id", UUID::class.java)
                                    ?: throw RuntimeException(
                                        "Couldn't find question ID for user session")),
                        questionDescription =
                            QuestionDescription(
                                row.get("q.description", String::class.java)
                                    ?: throw RuntimeException(
                                        "Couldn't find question description for user session")),
                        questionAnswer =
                            QuestionAnswer(
                                row.get("q.answer", String::class.java)
                                    ?: throw RuntimeException(
                                        "Couldn't find question answer for user session")),
                        questionTips = questionTips,
                        questionImage =
                            QuestionImage(
                                row.get("q.image", String::class.java)
                                    ?: throw RuntimeException(
                                        "Couldn't find question image for user session")),
                        questionDateOfAppearance =
                            QuestionDateOfAppearance(
                                row.get("q.date_appearance", LocalDate::class.java)
                                    ?: throw RuntimeException(
                                        "Couldn't find question date of appearance for user session")),
                    )
                Session(
                    userIdentifier = userIdentifier,
                    sessionStartedDate = SessionDate(LocalDate.now()),
                    sessionFinishedDate = null,
                    question = question,
                    showedTips = showedTips)
            }
            .one()
            .block()
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
            .fetch()
    }
}
