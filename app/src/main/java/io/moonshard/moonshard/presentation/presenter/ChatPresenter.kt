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
import java9.util.concurrent.CompletableFuture
import java9.util.stream.StreamSupport
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smackx.filetransfer.FileTransferManager
import org.jivesoftware.smackx.forward.packet.Forwarded
import org.jivesoftware.smackx.mam.MamManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.FullJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.stringprep.XmppStringprepException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>() {

    private val messageComparator =
        Comparator<GenericMessage> { o1, o2 -> o1.createdAt.time.compareTo(o2.createdAt.time) }

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
            viewState?.addToStart(myMessage, true)
            viewState?.cleanMessage()
            // EventBus.getDefault().post(LastMessageEvent(chatEntity!!.jid, message))
            // return true
        }
        // Toast.makeText(view.getActivityObject(), "Network error!", Toast.LENGTH_SHORT).show()
    }

    fun sendMessage(text: String) {
        if (chat?.isGroupChat!!) {
            sendMessageGroupChat(text)
        } else {
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
        try {
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)
            val jid = JidCreate.entityBareFrom(chatID)
            val muc = MainApplication.getXmppConnection()?.multiUserChatManager?.getMultiUserChat(jid)
            val mec = muc?.getEnterConfigurationBuilder(nickName)

            mec?.requestNoHistory()
            val mucEnterConfig = mec?.build()
            muc?.join(mucEnterConfig)
            muc?.addMessageListener(MainApplication.getXmppConnection().network)
        }catch (e:java.lang.Exception){
            //will add toast
        }
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

    fun sendFile(path: File) {
        if (MainApplication.getXmppConnection().isConnectionAlive) {
            val jid: FullJid?
            try {
                jid = JidCreate.entityFullFrom("$chatID/Smack")
                MainApplication.getXmppConnection().sendFile(jid,path)
            } catch (e: XmppStringprepException) {

            }
        }
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
                viewState?.addToStart(message, true)

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

    fun loadMoreMessages() {
        loadMessagesFromMAM().thenAccept { query ->
            if (query != null) {
                val adapterMessages = ArrayList<GenericMessage>()
                StreamSupport.stream(query.page.forwarded)
                    .forEach { forwardedMessage ->
                        val message =
                            Forwarded.extractMessagesFrom(Collections.singleton(forwardedMessage))[0]
                        if (message.body != null) {
                            if (LocalDBWrapper.getMessageByUID(message.stanzaId) == null) {
                                val messageID = LocalDBWrapper.createMessageEntry(
                                    chatID,
                                    message.stanzaId,
                                    message.from.asBareJid().asUnescapedString(),
                                    forwardedMessage.delayInformation.stamp.time,
                                    message.body,
                                    true,
                                    true
                                )
                                adapterMessages.add(
                                    GenericMessage(
                                        LocalDBWrapper.getMessageByID(
                                            messageID
                                        )
                                    )
                                )
                            }
                        }
                    }
                MainApplication.getMainUIThread().post {
                    adapterMessages.sortWith(messageComparator)
                    viewState?.addToEnd(adapterMessages, true)
                }
                if (query.messageCount != 0) {
                    chat!!.firstMessageUid = query.mamResultExtensions[0].id
                    LocalDBWrapper.updateChatEntity(chat)
                }
            }
        }
    }

    fun loadLocalMessages() {
        val entities = loadLocalMessagesLogic()
        val messages = ArrayList<GenericMessage>()
        if (entities != null) {
            entities.forEach {
                messages.add(GenericMessage(it))
            }
        }
        messages.sortWith(messageComparator)
        viewState.addToEnd(messages, true)
    }

    fun loadLocalMessagesLogic(): List<MessageEntity>? {
        return LocalDBWrapper.getMessagesByChatID(chatID)
    }

    fun loadRecentPageMessages() {
        loadRecentPageMessages2().thenAccept { query ->
            if (query != null) {
                val adapterMessages = ArrayList<GenericMessage>()
                StreamSupport.stream(query.page.forwarded)
                    .forEach { forwardedMessage ->
                        val message =
                            Forwarded.extractMessagesFrom(Collections.singleton(forwardedMessage))[0]
                        if (message.body != null) {
                            if (LocalDBWrapper.getMessageByUID(message.stanzaId) == null) {
                                val messageID = LocalDBWrapper.createMessageEntry(
                                    chatID,
                                    message.stanzaId,
                                    message.from.asBareJid().asUnescapedString(),
                                    forwardedMessage.delayInformation.stamp.time,
                                    message.body,
                                    true,
                                    true
                                )
                                adapterMessages.add(
                                    GenericMessage(
                                        LocalDBWrapper.getMessageByID(
                                            messageID
                                        )
                                    )
                                )
                            }
                        }
                    }
                MainApplication.getMainUIThread().post {
                    adapterMessages.sortWith(messageComparator)
                    adapterMessages.forEach {
                        viewState.addToStart(it, true)
                    }
                }
                if (query.messageCount != 0 && chat!!.firstMessageUid == "") {
                    chat?.firstMessageUid = query.mamResultExtensions[0].id
                    LocalDBWrapper.updateChatEntity(chat)
                }
                // EventBus.getDefault().post(LastMessageEvent(chatID, GenericMessage(LocalDBWrapper.getLastMessage(chatID))))
            }
        }
    }

    fun loadRecentPageMessages2(): CompletableFuture<MamManager.MamQuery?> {
        return CompletableFuture.supplyAsync {
            if (MainApplication.getXmppConnection() != null) {
                val mamManager: MamManager? = MainApplication.getXmppConnection().mamManager
                if (mamManager != null) {
                    return@supplyAsync mamManager.queryMostRecentPage(JidCreate.from(chatID), 20)
                } else {
                    return@supplyAsync null
                }
            } else {
                return@supplyAsync null
            }
        }
    }

    fun loadMessagesFromMAM(): CompletableFuture<MamManager.MamQuery?> {
        return CompletableFuture.supplyAsync {
            if (MainApplication.getXmppConnection() != null) {
                val mamManager: MamManager? = MainApplication.getXmppConnection().mamManager
                if (mamManager != null) {
                    val firstMessageUid = LocalDBWrapper.getChatByChatID(chatID).firstMessageUid
                    if (firstMessageUid != "") {
                        return@supplyAsync mamManager.queryArchive(
                            MamManager.MamQueryArgs.builder()
                                .beforeUid(firstMessageUid)
                                .limitResultsToJid(JidCreate.from(chatID))
                                .setResultPageSizeTo(50)
                                .build()
                        )
                    } else {
                        return@supplyAsync null
                    }
                } else {
                    return@supplyAsync null
                }
            } else {
                return@supplyAsync null
            }
        }
    }


}