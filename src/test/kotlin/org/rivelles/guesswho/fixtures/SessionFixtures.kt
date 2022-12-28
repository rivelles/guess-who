package org.rivelles.guesswho.fixtures

import java.util.UUID
import org.rivelles.guesswho.domain.*

fun aQuestion(): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(emptyList()))

fun anUserIdentifier(): UserIdentifier = UserIdentifier("168.0.0.1")
