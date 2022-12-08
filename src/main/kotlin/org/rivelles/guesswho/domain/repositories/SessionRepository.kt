package org.rivelles.guesswho.domain.repositories

import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.UserIdentifier

interface SessionRepository {
    fun findTodaySessionForUser(userIdentifier: UserIdentifier): Session?
    fun save(session: Session)
}