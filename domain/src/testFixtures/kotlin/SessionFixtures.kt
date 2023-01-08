package fixtures

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import UserIdentifier
import java.time.LocalDate
import java.util.Collections.emptyList
import java.util.UUID

fun aQuestionWithoutTips(): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(emptyList()),
        QuestionImage("http://fakeUrl.com/image"),
        QuestionDateOfAppearance(LocalDate.MAX))

fun aQuestionWithOneTip(tip: String): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(listOf(tip)),
        QuestionImage("http://fakeUrl.com/image"),
        QuestionDateOfAppearance(LocalDate.MAX))

fun anUserIdentifier(): UserIdentifier = UserIdentifier("168.0.0.1")
