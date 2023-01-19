package org.rivelles.adapters.postgresql

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import java.time.LocalDate
import java.util.*
import org.springframework.r2dbc.core.DatabaseClient
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
            .all()
            .map { rows ->
                val tips = rows["tip"].toString()
                val questionId = UUID.fromString(rows["question_id"].toString())
                val description = rows["description"].toString()
                val answer = rows["answer"].toString()
                val image = rows["image"].toString()
                val dateOfAppearance =
                    LocalDate.parse(rows["date_appearance"].toString().split("T")[0])

                Question(
                    QuestionId(questionId),
                    QuestionDescription(description),
                    QuestionAnswer(answer),
                    QuestionTips(listOf(tips)),
                    QuestionImage(image),
                    QuestionDateOfAppearance(dateOfAppearance))
            }
            .reduce { q1, q2 ->
                val questionTips =
                    QuestionTips(listOf(q1.questionTips.tips[0], q2.questionTips.tips[0]))
                q1.copy(questionTips = questionTips)
            }
            .toFuture()
            .get()
    }

    override fun save(question: Question) {
        databaseClient
            .sql {
                """
            INSERT INTO questions
            (id, description, answer, image, date_appearance)
            VALUES
            (:id, :description, :answer, :image, :date_appearance)
        """.trimIndent()
            }
            .bind("id", question.questionId.id)
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
            (id, question_id, tip)
            VALUES
            (:id, '${question.questionId.id}', :tip)
        """.trimIndent()
                }
                .bind("id", UUID.randomUUID().toString())
                .bind("tip", it)
                .fetch()
                .one()
                .toFuture()
                .get()
        }
    }
}
