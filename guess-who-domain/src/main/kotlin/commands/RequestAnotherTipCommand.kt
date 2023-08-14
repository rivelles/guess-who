package commands

import UserIdentifier

data class RequestAnotherTipCommand(val userIdentifier: UserIdentifier) : Command {}
