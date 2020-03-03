package io.moonshard.moonshard.presentation.presenter.chat.info

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.info.AddAdminView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate


@InjectViewState
class AddAdminPresenter : MvpPresenter<AddAdminView>() {

    fun addAdmin(user:String,jidChatString:String) {
        try {
            if(user.contains("@")){
                return
            }

            val groupId = JidCreate.entityBareFrom(jidChatString)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            muc.grantAdmin(JidCreate.from("$user@moonshard.tech"))
            viewState?.showChatScreen()
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }
}