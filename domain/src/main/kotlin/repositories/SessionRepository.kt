package repositories

import Session
import UserIdentifier

interface SessionRepository {
    fun findTodaySessionForUser(userIdentifier: UserIdentifier): Session?
    fun save(session: Session)
}
