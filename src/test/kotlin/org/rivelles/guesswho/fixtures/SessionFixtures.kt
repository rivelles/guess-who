package org.rivelles.guesswho.fixtures

import java.time.LocalDate
import java.util.UUID
import org.rivelles.guesswho.domain.*

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
