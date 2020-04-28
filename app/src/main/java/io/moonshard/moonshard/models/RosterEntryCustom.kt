package io.moonshard.moonshard.models

import org.jivesoftware.smack.roster.RosterEntry

class RosterEntryCustom(val contact: RosterEntry? = null,
                         var isSelected: Boolean = false)
