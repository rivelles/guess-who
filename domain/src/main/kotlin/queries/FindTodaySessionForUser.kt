package queries

import UserIdentifier

data class FindTodaySessionForUser(val userIdentifier: UserIdentifier) : Query
