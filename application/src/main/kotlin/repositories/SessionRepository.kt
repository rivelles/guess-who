package org.rivelles.adapters.persistence

import Session
import UserIdentifier
import reactor.core.publisher.Mono

interface SessionRepository {
    fun findTodaySessionForUser(userIdentifier: UserIdentifier): Mono<Session?>
    fun save(session: Session): Mono<Int>
}
