package org.rivelles.guesswho.domain.commands

interface CommandHandler<T : Command> {
    fun handle(command: T): Any
}
