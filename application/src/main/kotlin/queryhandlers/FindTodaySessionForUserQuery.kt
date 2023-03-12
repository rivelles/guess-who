package queryhandlers

import Session
import org.rivelles.adapters.persistence.SessionRepository
import queries.FindTodaySessionForUser
import reactor.core.publisher.Mono

class FindTodaySessionForUserQueryHandler(private val sessionRepository: SessionRepository) :
    QueryHandler<FindTodaySessionForUser> {
    override fun handle(query: FindTodaySessionForUser): Mono<Session?> {
        return sessionRepository.findTodaySessionForUser(query.userIdentifier)
    }
}
