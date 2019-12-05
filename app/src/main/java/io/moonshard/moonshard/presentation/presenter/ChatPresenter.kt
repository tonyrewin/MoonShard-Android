package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.instacart.library.truetime.TrueTime
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.moonshard.moonshard.presentation.view.ChatView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java9.util.concurrent.CompletableFuture
import java9.util.stream.StreamSupport
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.forward.packet.Forwarded
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.FullJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.stringprep.XmppStringprepException
import trikita.log.Log
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.collections.ArrayList

@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>() {
    private val messageComparator =
        Comparator<GenericMessage> { o1, o2 -> o1.createdAt.time.compareTo(o2.createdAt.time) }
    private lateinit var chatID: String
    private lateinit var chat: ChatEntity
    private var onNewMessageDisposable: Disposable? = null

    @SuppressLint("CheckResult")
    fun setChatId(chatId: String) {
        chatID = chatId // FIXME remove hardcode
        ChatListRepository.getChatByJid(JidCreate.bareFrom(chatId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                chat = it
                loadLocalMessages()
                loadMoreMessages()
                getDataInfo()
            }
    }

    @SuppressLint("CheckResult")
    fun sendMessage(text: String) {
        sendMessageInternal(text)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val myMessage = GenericMessage(it)
                viewState?.addToStart(myMessage, true)
                //viewState?.addMessage(it)
                viewState?.cleanMessage()
            }, {
                // TODO add error handling
            })
    }

    fun getDataInfo(){
        try {
            val groupId = JidCreate.entityBareFrom(chatID)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val roomInfo =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(groupId)
            val occupants = muc.occupants

            val name = roomInfo.name
            getAvatar(chatID)
            val valueOccupants = roomInfo.occupantsCount
            val valueOnlineMembers = getValueOnlineUsers(muc,occupants)
            viewState?.setData(name,valueOccupants,valueOnlineMembers)
        }catch (e:Exception){

        }
    }

    private fun getValueOnlineUsers(muc: MultiUserChat, members: List<EntityFullJid>): Int {
        var onlineValue = 0
        for (i in members.indices) {
            val userOccupantPresence = muc.getOccupantPresence(members[i].asEntityFullJidIfPossible())
            if (userOccupantPresence.type == Presence.Type.available) {
                onlineValue++
            }
        }
        return onlineValue
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
            var error = ""
        }
    }

    private fun sendMessageInternal(text: String): Single<MessageEntity> {
        return Single.create {
            val jid: EntityBareJid
            try {
                jid = JidCreate.entityBareFrom(chatID)
            } catch (e: XmppStringprepException) {
                it.onError(e)
                return@create
            }

            val messageUid = if (chat.isGroupChat) MainApplication.getXmppConnection().sendMessageGroupChat(jid, text)
                                    else MainApplication.getXmppConnection().sendMessage(jid, text)

            while (!TrueTime.isInitialized()) {
                Thread {
                    try {
                        TrueTime.build().initialize()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.start()
            }

            val timestamp = try {
                TrueTime.now().time
            } catch (e: Exception) {
                // Fallback to Plain Old Java CurrentTimeMillis
                System.currentTimeMillis()
            }

            val message = MessageEntity(
                messageUid = messageUid,
                timestamp = timestamp,
                text = text,
                isSent = false,
                isRead = false,
                isCurrentUserSender = true
            )
            // message.sender = ??? FIXME
            message.chat.target = this.chat
            MessageRepository.saveMessage(message).subscribe {
                it.onSuccess(message)
            }
        }
    }

    fun sendFile(path: File) {
        if (MainApplication.getXmppConnection().isConnectionReady) {
            val jid: FullJid?
            try {
                jid = JidCreate.entityFullFrom("$chatID/Smack")
                MainApplication.getXmppConnection().sendFile(jid,path)
            } catch (e: XmppStringprepException) {

            }
        }
    }

    override fun onDestroy() {
        onNewMessageDisposable?.dispose()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        MainApplication.getXmppConnection().network.subscribeOnMessage(onNewMessage())
    }

    private fun onNewMessage(): Observer<MessageEntity> {
        return object : Observer<MessageEntity> {

            override fun onSubscribe(d: Disposable) {
                onNewMessageDisposable = d
            }

            override fun onNext(message: MessageEntity) {
                ChatListRepository.updateUnreadMessagesCountByJid(JidCreate.bareFrom(chat.jid), 0)
                viewState?.addToStart(GenericMessage(message), true)
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
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

    @SuppressLint("CheckResult")
    fun loadMoreMessages() {
        loadMessagesFromMAM().thenAccept { query ->
            if (query != null) {
                val adapterMessages = ArrayList<GenericMessage>()
                StreamSupport.stream(query.page.forwarded)
                    .forEach { forwardedMessage ->
                        val message =
                            Forwarded.extractMessagesFrom(Collections.singleton(forwardedMessage))[0]
                        if (message.body != null) {
                            MessageRepository.getMessageById(message.stanzaId)
                                .observeOn(Schedulers.io())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    // FIXME
                                }, {
                                    // FIXME
                                })
                        }
                    }
                MainApplication.getMainUIThread().post {
                    adapterMessages.sortWith(messageComparator)
                    viewState?.addToEnd(adapterMessages, true)
                }
                if (query.messageCount != 0) {
                    /*chat!!.firstMessageUid = query.mamResultExtensions[0].id
                    LocalDBWrapper.updateChatEntity(chat)*/
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun loadLocalMessages() {
        loadLocalMessagesLogic().subscribe { messages ->
            val genericMessages = ArrayList<GenericMessage>()
            if (messages.isNotEmpty()) {
                messages.forEach {
                    genericMessages.add(GenericMessage(it))
                }
            }
            genericMessages.sortWith(messageComparator)
            viewState.addToEnd(genericMessages, true)
        }
    }

    private fun loadLocalMessagesLogic(): Observable<List<MessageEntity>> {
        return MessageRepository.getMessagesByJid(JidCreate.bareFrom(chat.jid))
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
                            /*if (LocalDBWrapper.getMessageByUID(message.stanzaId) == null) {
                                *//*val messageID = LocalDBWrapper.createMessageEntry(
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
                                )*//*
                            }*/
                        }
                    }
                MainApplication.getMainUIThread().post {
                    adapterMessages.sortWith(messageComparator)
                    adapterMessages.forEach {
                        viewState.addToStart(it, true)
                    }
                }
                /*if (query.messageCount != 0 && chat!!.firstMessageUid == "") {
                    chat?.firstMessageUid = query.mamResultExtensions[0].id
                    LocalDBWrapper.updateChatEntity(chat)
                }*/
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
            /*if (MainApplication.getXmppConnection() != null) {
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
            }*/
            return@supplyAsync null
        }
    }
/*
    private fun getAvatar(jid: String): Bitmap? {
        var avatarBytes: ByteArray? = ByteArray(0)
        try {
            val future =
                MainApplication.getXmppConnection().network.loadAvatar(jid)

            if (future != null) {
                avatarBytes = future.get()
            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        var avatar: Bitmap?=null
        if (avatarBytes != null) {
            avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
        }
        return avatar
    }

 */

    private fun getAvatar(jid: String) {
            MainApplication.getXmppConnection().loadAvatar(jid)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    if (bytes != null) {
                        val avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        viewState?.setAvatar(avatar)
                    }
                }, { throwable ->
                    Log.e(throwable.message)
                })
    }


}