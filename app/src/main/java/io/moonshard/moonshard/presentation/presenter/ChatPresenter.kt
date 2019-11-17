package io.moonshard.moonshard.presentation.presenter

import com.instacart.library.truetime.TrueTime
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.models.roomEntities.ChatEntity
import io.moonshard.moonshard.models.roomEntities.MessageEntity
import io.moonshard.moonshard.presentation.view.ChatView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.stringprep.XmppStringprepException
import java.io.IOException


@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>() {

    var chatID: String? = null
    var chat: ChatEntity? = null
    fun setChatId(chatId: String) {
        this.chatID = chatId
        chat = LocalDBWrapper.getChatByChatID(chatId)
    }

    fun sendMessageOneToOneChat(text: String) {
        val message: MessageEntity? = sendMessageOneToOne(text)
        if (message != null) {
            val myMessage = GenericMessage(message)
            viewState?.addMessage(myMessage)
            viewState?.cleanMessage()
            // EventBus.getDefault().post(LastMessageEvent(chatEntity!!.jid, message))
            // return true
        }
        // Toast.makeText(view.getActivityObject(), "Network error!", Toast.LENGTH_SHORT).show()
    }

    fun sendMessage(text: String) {
        if(chat?.isGroupChat!!){
            sendMessageGroupChat(text)
        }else{
            sendMessageOneToOneChat(text)
        }
    }

    fun sendMessageGroupChat(text: String) {
        val message: MessageEntity? = sendMessageGroup(text)
        if (message != null) {
            val myMessage = GenericMessage(message)
            //viewState?.addMessage(myMessage)
        }
        viewState?.cleanMessage()
    }

    fun join() {
        val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)
        val jid = JidCreate.entityBareFrom(chatID)
        val muc = MainApplication.getXmppConnection().multiUserChatManager.getMultiUserChat(jid)
        muc.join(nickName)
        muc.addMessageListener(MainApplication.getXmppConnection().network)
    }

    fun sendMessageGroup(text: String): MessageEntity? {
        val jid: EntityBareJid
        try {
            jid = JidCreate.entityBareFrom(chatID)
        } catch (e: XmppStringprepException) {
            return null
        }

        val messageUid = MainApplication.getXmppConnection().sendMessageGroupChat(jid, text)
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
            chatID,
            messageUid,
            MainApplication.getJid(),
            timestamp,
            text,
            true,
            false
        )
        return LocalDBWrapper.getMessageByID(messageID)
    }

    fun sendMessageOneToOne(text: String): MessageEntity? {
        if (MainApplication.getXmppConnection().isConnectionAlive) {
            val jid: EntityBareJid
            try {
                jid = JidCreate.entityBareFrom(chatID)
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
                chatID,
                messageUid,
                MainApplication.getJid(),
                timestamp,
                text,
                true,
                false
            )
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