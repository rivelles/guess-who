package commands

interface CommandHandler<T : Command> {
    fun handle(command: T): Any
}
