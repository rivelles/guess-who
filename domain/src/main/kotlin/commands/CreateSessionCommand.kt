package commands

import UserIdentifier

data class CreateSessionCommand(val userIdentifier: UserIdentifier) : Command
