package io.moonshard.moonshard.presentation.presenter

import com.instacart.library.truetime.TrueTime
import io.moonshard.moonshard.helpers.AppHelper
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.roomEntities.MessageEntity
import io.moonshard.moonshard.presentation.view.ChatView
import io.moonshard.moonshard.presentation.view.LoginView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.chat2.Chat
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException
import java.io.IOException

@InjectViewState
class ChatPresenter: MvpPresenter<ChatView>() {

    fun sendMessage2(text:String){

    }



     fun sendMessage(text: String): MessageEntity? {
        if (AppHelper.getXmppConnection().isConnectionAlive) {
            val jid: EntityBareJid
            try {
                jid = JidCreate.entityBareFrom("just@moonshard.tech")
            } catch (e: XmppStringprepException) {
                return null
            }

            val messageUid = AppHelper.getXmppConnection().sendMessage(jid, text)
            while (!TrueTime.isInitialized()) {
                Thread {
                    try {
                        TrueTime.build().initialize()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.start()
            }

            var timestamp: Long
            try {
                timestamp = TrueTime.now().time
            } catch (e: Exception) {
                // Fallback to Plain Old Java CurrentTimeMillis
                timestamp = System.currentTimeMillis()
            }

            val messageID = LocalDBWrapper.createMessageEntry("1", messageUid, AppHelper.getJid(), timestamp, text, true, false)
            return LocalDBWrapper.getMessageByID(messageID)
        } else {
            return null
        }
    }



    fun sendChat(){

    }

}