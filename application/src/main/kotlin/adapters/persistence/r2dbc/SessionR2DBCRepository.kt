package org.rivelles.adapters.persistence.r2dbc

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
import org.rivelles.adapters.persistence.SessionRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class SessionR2DBCRepository(val databaseClient: DatabaseClient) : SessionRepository {
    override fun findTodaySessionForUser(userIdentifier: UserIdentifier): Mono<Session?> {
        val today = LocalDate.now()
        return databaseClient
            .sql {
                """
                        SELECT s.id, s.question_id, user_identifier, session_started_date, session_finished_date, description, answer, image, date_appearance, tip, tip_id FROM sessions s 
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
            .mapNotNull { it.toSession() }
            .reduce { session1, session2 -> session1?.mergeTips(session2) }
    }

    override fun save(session: Session): Mono<Int> {
        return databaseClient
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
            .rowsUpdated()
    }

    private fun Session?.mergeTips(session: Session?): Session? {
        if (this == null || session == null) return null
        val newQuestionTips = this.question.questionTips.tips + session.question.questionTips.tips
        val newShowedTips = this.showedTips.tips + session.showedTips.tips

        return this.copy(
            question = this.question.copy(questionTips = QuestionTips(newQuestionTips)),
            showedTips = this.showedTips.copy(newShowedTips))
    }
    private fun MutableMap<String, Any>.toQuestion(): Question {
        val questionId = UUID.fromString(this["question_id"].toString())
        val description = this["description"].toString()
        val answer = this["answer"].toString()
        val image = this["image"].toString()
        val dateOfAppearance = LocalDate.parse(this["date_appearance"].toString().split("T")[0])
        val tip = this["tip"]?.toString()

        return Question(
            QuestionId(questionId),
            QuestionDescription(description),
            QuestionAnswer(answer),
            QuestionTips(tip?.let { listOf(it) } ?: emptyList()),
            QuestionImage(image),
            QuestionDateOfAppearance(dateOfAppearance))
    }
    private fun MutableMap<String, Any>.toSession(): Session? {
        if (this.isEmpty()) return null
        val question = this.toQuestion()

        val userIdentifier = this["user_identifier"].toString()
        val sessionStartedDate =
            LocalDate.parse(this["session_started_date"].toString().split("T")[0])
        val sessionFinishedDate =
            this["session_finished_date"]?.let { LocalDate.parse(it.toString().split("T")[0]) }

        return Session(
            UserIdentifier(userIdentifier),
            SessionDate(sessionStartedDate),
            sessionFinishedDate?.let { SessionDate(it) },
            question,
            QuestionTips(
                question.questionTips.tips.takeIf { this.containsShowedTip() } ?: emptyList()))
    }

    fun MutableMap<String, Any>.containsShowedTip() = this["tip_id"] != null
}
