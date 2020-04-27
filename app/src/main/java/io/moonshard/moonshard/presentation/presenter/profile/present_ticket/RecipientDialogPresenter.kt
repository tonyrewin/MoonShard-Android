package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.BasePresenter
import io.moonshard.moonshard.presentation.view.profile.present_ticket.RecipientDialogiView
import moxy.InjectViewState
import org.jivesoftware.smack.roster.RosterEntry

@InjectViewState
class RecipientDialogPresenter : BasePresenter<RecipientDialogiView>(){

    fun getContacts() {
        val usersSet = MainApplication.getXmppConnection().contactList

        val contacts = ArrayList<RosterEntry>()
        contacts.addAll(usersSet)

        viewState?.showContacts(contacts)
    }
}