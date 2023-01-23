package org.rivelles.adapters.postgresql

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
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Repository
import repositories.SessionRepository

@Repository
class SessionR2DBCRepository(val databaseClient: DatabaseClient) : SessionRepository {
    override fun findTodaySessionForUser(userIdentifier: UserIdentifier): Session? {
        val today = LocalDate.now()
        return runBlocking {
            databaseClient
                .sql {
                    """
            SELECT * FROM sessions s 
            INNER JOIN questions q ON s.question_id = q.id 
            LEFT OUTER JOIN question_tips qt ON q.id = qt.question_id 
            LEFT OUTER JOIN showed_tips st ON s.id = st.session_id 
            WHERE s.user_identifier = :user_identifier  
            AND s.session_started_date = :session_started_date
        """.trimIndent()
                }
                .bind("user_identifier", userIdentifier.userIp)
                .bind("session_started_date", today)
                .fetch()
                .all()
                .map { row ->
                    val userIdentifier = row["user_identifier"].toString()
                    val sessionStartedDate =
                        LocalDate.parse(row["session_started_date"].toString().split("T")[0])
                    val sessionFinishedDate =
                        row["session_finished_date"]?.let {
                            LocalDate.parse(it.toString().split("T")[0])
                        }

                    val tips = row["tip"].toString()
                    val questionId = UUID.fromString(row["q.id"].toString())
                    val description = row["description"].toString()
                    val answer = row["answer"].toString()
                    val image = row["image"].toString()
                    val dateOfAppearance =
                        LocalDate.parse(row["date_appearance"].toString().split("T")[0])

                    val isTipShowed = row["tip_id"] != null

                    val question =
                        Question(
                            QuestionId(questionId),
                            QuestionDescription(description),
                            QuestionAnswer(answer),
                            QuestionTips(listOf(tips)),
                            QuestionImage(image),
                            QuestionDateOfAppearance(dateOfAppearance))

                    Session(
                        UserIdentifier(userIdentifier),
                        SessionDate(sessionStartedDate),
                        sessionFinishedDate?.let { SessionDate(it) },
                        question,
                        QuestionTips(
                            tips.takeIf { isTipShowed }?.let { listOf(it) } ?: emptyList()))
                }
                .reduce { session1, session2 ->
                    val newQuestionTips =
                        session1.question.questionTips.tips + session2.question.questionTips.tips
                    val newShowedTips = session1.showedTips.tips + session2.showedTips.tips

                    session1.copy(
                        question =
                            session1.question.copy(questionTips = QuestionTips(newQuestionTips)),
                        showedTips = session1.showedTips.copy(newShowedTips))
                }
                .awaitSingleOrNull()
        }
    }

    override fun save(session: Session) {
        runBlocking {
            databaseClient
                .sql {
                    """
            INSERT INTO sessions (id, user_identifier, session_started_date, question_id) 
            VALUES (:id, :user_identifier, :session_started_date, :question_id)
        """.trimIndent()
                }
                .bind("id", UUID.randomUUID().toString())
                .bind("user_identifier", session.userIdentifier.userIp)
                .bind("session_started_date", session.sessionStartedDate.date)
                .bind("question_id", session.question.questionId.id.toString())
                .fetch()
                .awaitRowsUpdated()
        }
    }
}
