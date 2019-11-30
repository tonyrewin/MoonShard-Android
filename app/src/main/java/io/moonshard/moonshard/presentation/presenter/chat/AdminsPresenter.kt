package io.moonshard.moonshard.presentation.presenter.chat

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.AdminsView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class AdminsPresenter : MvpPresenter<AdminsView>() {

    fun getAdmins(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators
            viewState?.showAdmins(moderators)
        } catch (e: java.lang.Exception) {
            e.message?.let { viewState?.showToast(it) }
        }
    }

}