package io.moonshard.moonshard.presentation.view.profile.wallet.transfer

import moxy.MvpView
import org.jivesoftware.smack.roster.RosterEntry

interface TransferRecipientDialogView:MvpView {
    fun showContacts(contacts: ArrayList<RosterEntry>)

}