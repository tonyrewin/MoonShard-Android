package io.moonshard.moonshard.presentation.view.profile.present_ticket

import io.moonshard.moonshard.models.RosterEntryCustom
import io.moonshard.moonshard.models.jabber.Recipient
import moxy.MvpView
import org.jivesoftware.smack.roster.RosterEntry

interface RecipientDialogiView:MvpView {
    fun showContacts(contacts: ArrayList<Recipient>)
    fun back()
    fun dismissBack()
    fun showToast(text: String)
    fun showProgressBar()
    fun hideProgressBar()
    fun onDataChange()
}