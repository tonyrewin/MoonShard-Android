package io.moonshard.moonshard.presentation.presenter.chat

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.ManageChatView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart


@InjectViewState
class ManageChatPresenter: MvpPresenter<ManageChatView>() {

    fun setNewNameChat(name:String,jid: String){
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)

          //  val resourcepartNickname = JidCreate.bareFrom(name+"@conference.moonshard.tech").resourceOrEmpty
           // muc.changeNickname(Resourcepart.from(name))
        } catch (e: Exception) {
            val test = ""
           // e.message?.let { viewState?.showToast(it) }
        }
    }
}