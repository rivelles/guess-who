import java.time.LocalDate

data class SessionDate(val date: LocalDate) {
    constructor() : this(LocalDate.now())
}
