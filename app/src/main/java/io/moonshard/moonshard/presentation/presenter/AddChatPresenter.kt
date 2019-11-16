package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.jabber.GenericUser
import io.moonshard.moonshard.presentation.view.AddChatView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

@InjectViewState
class AddChatPresenter : MvpPresenter<AddChatView>() {

    fun createChat(username:String){
        if(!username.contains("@")) {
            viewState?.showError("Должен содержать @ host")
            return
        }
        try {
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val entityBareJid = JidCreate.entityBareFrom(username)
            val muc = manager.getMultiUserChat(entityBareJid)
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)
            muc.create(nickName).makeInstant()
            muc.join(nickName)
            LocalDBWrapper.createChatEntry(username, username.split("@")[0], ArrayList<GenericUser>())
        } catch (e: Exception) {
            viewState?.showError(e.message!!)
        }
    }
}