package org.rivelles.guesswho.domain.commands

import org.rivelles.guesswho.domain.UserIdentifier

data class CreateSessionCommand(val userIdentifier: UserIdentifier) : Command
