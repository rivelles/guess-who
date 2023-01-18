package org.rivelles.adapters.postgresql

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import io.r2dbc.spi.Statement
import java.time.LocalDate
import java.util.*
import kotlin.collections.List
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.ExecuteFunction
import org.springframework.stereotype.Repository
import repositories.QuestionRepository

@Repository
class QuestionR2DBCRepository(val databaseClient: DatabaseClient) : QuestionRepository {
    override fun getQuestionOfTheDay(): Question? {
        val today = LocalDate.now()
        return databaseClient
            .sql {
                """
            SELECT * FROM questions q
            INNER JOIN question_tips qt ON qt.question_id = q.id
            WHERE date_appearance = :date_appearance
        """.trimIndent()
            }
            .bind("date_appearance", today)
            .fetch()
            .one()
            .map { rows ->
                val tipsList = rows["tip"]
                var questionTips = mutableListOf<String>()
                if (tipsList is List<*>) {
                    questionTips = tipsList.stream().map { it.toString() }.toList()
                }
                val questionId = UUID.fromString(rows["external_id"].toString())
                val description = rows["description"].toString()
                val answer = rows["answer"].toString()
                val image = rows["image"].toString()
                val dateOfAppearance = LocalDate.parse(rows["date_appearance"].toString())

                Question(
                    QuestionId(questionId),
                    QuestionDescription(description),
                    QuestionAnswer(answer),
                    QuestionTips(questionTips),
                    QuestionImage(image),
                    QuestionDateOfAppearance(dateOfAppearance))
            }
            .toFuture()
            .get()
    }

    override fun save(question: Question) {
        val questionId =
            databaseClient
                .sql {
                    """
            INSERT INTO questions
            (external_id, description, answer, image, date_appearance)
            VALUES
            (:external_id, :description, :answer, :image, :date_appearance)
        """.trimIndent()
                }
                .filter { statement: Statement?, next: ExecuteFunction ->
                    statement!!.returnGeneratedValues("id").execute()
                }
                .bind("external_id", question.questionId.id)
                .bind("description", question.questionDescription.description)
                .bind("answer", question.questionAnswer.answer)
                .bind("image", question.questionImage.imageUrl)
                .bind("date_appearance", question.questionDateOfAppearance.dateOfAppearance)
                .fetch()
                .one()
                .toFuture()
                .get()

        // TODO: Improve bulk insert
        question.questionTips.tips.forEach {
            databaseClient
                .sql {
                    """
            INSERT INTO question_tips
            (external_id, question_id, tip)
            VALUES
            (:external_id, ${questionId.get("id")}, :tip)
        """.trimIndent()
                }
                .bind("external_id", question.questionId.id)
                .bind("tip", it)
                .fetch()
                .one()
                .toFuture()
                .get()
        }
    }
}
