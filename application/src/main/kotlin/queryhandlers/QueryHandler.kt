package queryhandlers

import queries.Query

interface QueryHandler<T : Query> {
    fun handle(query: T): Any
}
