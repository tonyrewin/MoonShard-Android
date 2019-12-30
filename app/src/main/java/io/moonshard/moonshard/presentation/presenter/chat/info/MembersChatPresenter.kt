package io.moonshard.moonshard.presentation.presenter.chat.info

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.MembersChatView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class MembersChatPresenter: MvpPresenter<MembersChatView>() {

    fun getMembers(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val members = muc.occupants
            viewState?.showMembers(members)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun kickUser(jidChat:String,member: EntityFullJid) {
        try {
            val groupId = JidCreate.entityBareFrom(jidChat)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            muc.kickParticipant(member.resourceOrEmpty,"Without reason")
            viewState?.removeMember(member)
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }
}