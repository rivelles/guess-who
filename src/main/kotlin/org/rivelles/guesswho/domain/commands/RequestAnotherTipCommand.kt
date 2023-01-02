package org.rivelles.guesswho.domain.commands

import org.rivelles.guesswho.domain.UserIdentifier

data class RequestAnotherTipCommand(val userIdentifier: UserIdentifier) : Command {}
