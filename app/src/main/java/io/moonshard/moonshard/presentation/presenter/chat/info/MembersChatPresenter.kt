package io.moonshard.moonshard.presentation.presenter.chat.info

import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.MembersChatView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MUCAffiliation
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class MembersChatPresenter : MvpPresenter<MembersChatView>() {

    fun getMembers(jid: String) {
        try {
            viewState?.showProgressBar()
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val members = muc.occupants
            val occupants = arrayListOf<Occupant>()

            for (i in members.indices) {
                occupants.add(muc.getOccupant(members[i]))
            }

            val myJid = SecurePreferences.getStringValue("jid", null)


            val iterator = occupants.iterator()
            while (iterator.hasNext()) {
                val occupant = iterator.next()
                if (occupant.jid == null) {
                    iterator.remove()
                } else if (occupant.jid.asBareJid().asUnescapedString() == myJid) {
                    //iterator.remove()
                }
            }
            viewState?.showMembers(occupants)
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun kickUser(jidChat: String, jidUserInChat: EntityFullJid, fullUser: Occupant) {
        try {
            val groupId = JidCreate.entityBareFrom(jidChat)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)

            if (fullUser.affiliation == MUCAffiliation.admin) {
                muc.revokeAdmin(jidUserInChat)
            } else if (fullUser.affiliation == MUCAffiliation.member) {
                muc.revokeMembership(jidUserInChat)
            }

            muc.kickParticipant(jidUserInChat.resourceOrEmpty, "Without reason")

            viewState?.removeMember(fullUser)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }
}