package org.rivelles.adapters.http.requests

data class AnswerQuestionForSessionRequest(val userIdentifier: String, val providedAnswer: String)
