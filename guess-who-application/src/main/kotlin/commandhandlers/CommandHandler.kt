package org.rivelles.commandhandlers

import commands.Command

interface CommandHandler<T : Command> {
    fun handle(command: T): Any
}
