package org.rivelles.guesswho.domain

import java.time.LocalDate

data class SessionDate(val date: LocalDate) {
    constructor() : this(LocalDate.now())
}
