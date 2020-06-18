package io.moonshard.moonshard.presentation.view.profile.wallet.transfer

import io.moonshard.moonshard.models.jabber.Recipient
import moxy.MvpView
import org.jivesoftware.smack.roster.RosterEntry

interface TransferRecipientDialogView:MvpView {
    fun showContacts(contacts: ArrayList<Recipient>)
    fun showToast(text:String)
    fun showProgressBar()
    fun hideProgressBar()
}