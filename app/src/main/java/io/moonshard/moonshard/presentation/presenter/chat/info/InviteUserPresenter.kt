package io.moonshard.moonshard.presentation.presenter.chat.info

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.info.InviteUserView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate


@InjectViewState
class InviteUserPresenter: MvpPresenter<InviteUserView>() {

    fun inviteUser(user:String,jidChatString:String){
        try {
            if(user.contains("@")){
                // TODO: move message code to InviteUserView
                viewState.showError("Invalid character")
                return
            }

            val chatJid = JidCreate.entityBareFrom(jidChatString)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(chatJid)
            // TODO: move message code ...
            muc.invite(JidCreate.entityBareFrom("$user@moonshard.tech"),"Invitation")

            viewState?.showChatScreen()
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }
}