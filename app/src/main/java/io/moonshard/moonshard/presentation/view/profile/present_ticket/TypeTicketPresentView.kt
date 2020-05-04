package io.moonshard.moonshard.presentation.view.profile.present_ticket

import io.moonshard.moonshard.models.RosterEntryCustom
import moxy.MvpView
import org.jivesoftware.smack.roster.RosterEntry

interface TypeTicketPresentView: MvpView {

     fun showContacts(contacts: ArrayList<RosterEntry>)
}