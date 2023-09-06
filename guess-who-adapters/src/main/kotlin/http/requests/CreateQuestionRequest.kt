package org.rivelles.http.requests

import java.time.LocalDate

data class CreateQuestionRequest(
    val description: String,
    val answer: String,
    val tips: List<String>,
    val image: String,
    val dateAppearance: LocalDate
)
