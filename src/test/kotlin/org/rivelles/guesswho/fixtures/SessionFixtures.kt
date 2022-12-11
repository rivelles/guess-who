package org.rivelles.guesswho.fixtures

import org.rivelles.guesswho.domain.*
import java.util.UUID

fun aQuestion(): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(emptyList())
    )

fun anUserIdentifier(): UserIdentifier = UserIdentifier("168.0.0.1")