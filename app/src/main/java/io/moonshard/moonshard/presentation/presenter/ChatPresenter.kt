package io.moonshard.moonshard.presentation.presenter

import com.instacart.library.truetime.TrueTime
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.models.roomEntities.MessageEntity
import io.moonshard.moonshard.presentation.view.ChatView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException
import java.io.IOException


@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>() {

    var chatID:String?=null
    fun setChatId(chatId:String){
        this.chatID = chatId
    }

    fun sendMessage(text: String): MessageEntity? {
        if (MainApplication.getXmppConnection().isConnectionAlive) {
            val jid: EntityBareJid
            try {
                jid = JidCreate.entityBareFrom("testings@moonshard.tech")
            } catch (e: XmppStringprepException) {
                return null
            }

            val messageUid = MainApplication.getXmppConnection().sendMessage(jid, text)
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

            val messageID = LocalDBWrapper.createMessageEntry(
                "1",
                messageUid,
                MainApplication.getJid(),
                timestamp,
                text,
                true,
                false
            )


            val message = LocalDBWrapper.getMessageByID(messageID)

            if (message != null) {
                val myMessage = GenericMessage(message)
                // chatAdapter.addToStart(GenericMessage(LocalDBWrapper.getMessageByID(idMessage)), true)
                // LocalDBWrapper.updateChatUnreadMessagesCount(chatEntity.jid, 0)
                viewState?.addMessage(myMessage)
            }
            return LocalDBWrapper.getMessageByID(messageID)
        } else {
            return null
        }
    }

    override fun onDestroy() {
        MainApplication.getXmppConnection().network.unSubscribeOnMessage()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        MainApplication.getXmppConnection().network.subscribeOnMessage(onNewMessage())
    }

    private fun onNewMessage(): Observer<Long> {
        return object : Observer<Long> {

            override fun onSubscribe(d: Disposable) {
                var kek = ""
            }

            override fun onNext(idMessage: Long) {
                 //   if(idMessage.equals(chatID)) {
                        val message = GenericMessage(LocalDBWrapper.getMessageByID(idMessage))
                       // chatAdapter.addToStart(GenericMessage(LocalDBWrapper.getMessageByID(idMessage)), true)
                       // LocalDBWrapper.updateChatUnreadMessagesCount(chatEntity.jid, 0)
                        viewState?.addMessage(message)

                  //  }
            }

            override fun onError(e: Throwable) {
                var kek = ""
            }

            override fun onComplete() {
                var kek = ""
            }
        }
    }

    /*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onNewMessage(event: NewMessageEvent) {
        if(event.chatID.equals(chatEntity!!.jid)) {
            val messageID = event.messageID
            chatAdapter.addToStart(GenericMessage(LocalDBWrapper.getMessageByID(messageID)), true)
            LocalDBWrapper.updateChatUnreadMessagesCount(chatEntity.jid, 0)
        }
    }

     */
}