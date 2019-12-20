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

        if(user.contains("@")){
            viewState.showError("Вы ввели недопустимый символ")
            return
        }

        val chatJid = JidCreate.entityBareFrom(jidChatString)
        val muc =
            MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                .getMultiUserChat(chatJid)

        muc.invite(JidCreate.entityBareFrom("$user@moonshard.tech"),"Приглашение в чат")

       viewState?.showChatScreen()
    }
}