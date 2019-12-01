package io.moonshard.moonshard.presentation.presenter.chat

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.MembersChatView
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate

class MembersChatPresenter: MvpPresenter<MembersChatView>() {

    fun getMembers(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators
            viewState?.showMembers(moderators)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }
}