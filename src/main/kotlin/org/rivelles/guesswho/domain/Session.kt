package org.rivelles.guesswho.domain

data class Session constructor(
    val userIdentifier: UserIdentifier,
    val sessionDate: SessionDate,
    val question: Question) {
}
