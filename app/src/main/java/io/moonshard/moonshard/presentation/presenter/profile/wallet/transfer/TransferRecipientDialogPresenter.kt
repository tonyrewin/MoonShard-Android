package io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.profile.wallet.transfer.TransferRecipientDialogView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.roster.RosterEntry

@InjectViewState
class TransferRecipientDialogPresenter: MvpPresenter<TransferRecipientDialogView>() {

    fun getContacts() {
        val usersSet = MainApplication.getXmppConnection().contactList

        val contacts = ArrayList<RosterEntry>()
        contacts.addAll(usersSet)

        viewState?.showContacts(contacts)
    }

}