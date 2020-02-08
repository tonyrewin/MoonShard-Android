package io.moonshard.moonshard.presentation.presenter.chat

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import com.instacart.library.truetime.TrueTime
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.dbEntities.ChatUser
import io.moonshard.moonshard.models.dbEntities.MessageEntity
import io.moonshard.moonshard.presentation.view.MessagesView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.ChatUserRepository
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
import org.jivesoftware.smackx.forward.packet.Forwarded
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.FullJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.stringprep.XmppStringprepException
import trikita.log.Log
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@InjectViewState
class MessagesPresenter : MvpPresenter<MessagesView>() {
    private val messageComparator =
        Comparator<GenericMessage> { o1, o2 -> o1.createdAt.time.compareTo(o2.createdAt.time) }
    private lateinit var chatID: String
    private lateinit var chat: ChatEntity
    private var onNewMessageDisposable: Disposable? = null

    @SuppressLint("CheckResult")
    fun setChatId(chatId: String) {
        viewState?.showProgressBar()
        chatID = chatId
        ChatListRepository.getChatByJid(JidCreate.bareFrom(chatId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                chat = it
                loadLocalMessages()
               // loadMoreMessages() // FIXME
            }, {
                com.orhanobut.logger.Logger.d(it.message)
            })
        MessageRepository.updateRealUnreadMessagesCount(chatId).subscribe() // FIXME
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
                val error = ""
                // TODO add error handling
            })
    }

    fun join() {
        try {

            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = Resourcepart.from(card.nickName)

            val jid = JidCreate.entityBareFrom(chatID)
            val muc =
                MainApplication.getXmppConnection()?.multiUserChatManager?.getMultiUserChat(jid)
            val mec = muc?.getEnterConfigurationBuilder(nickName)

         //   mec?.requestNoHistory()
            val mucEnterConfig = mec?.build()
            muc?.join(mucEnterConfig)
            muc?.addMessageListener(MainApplication.getXmppConnection().network)
        } catch (e: java.lang.Exception) {
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

            val messageUid =
                if (chat.isGroupChat) MainApplication.getXmppConnection().sendMessageGroupChat(
                    jid,
                    text
                )
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
                messageUid = UUID.randomUUID().toString(),
                stanzaId = messageUid,
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
                MainApplication.getXmppConnection().sendFile(jid, path)
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
                    .subscribe()
                viewState?.addToStart(GenericMessage(message), true)
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        }
    }

    @SuppressLint("CheckResult")
    fun loadMoreMessages() {
        loadMessagesFromMAM()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ query ->
                if (query != null) {
                    val adapterMessages = ArrayList<GenericMessage>()
                    query.page.forwarded
                        .forEach { forwardedMessage ->
                            val message =
                                Forwarded.extractMessagesFrom(Collections.singleton(forwardedMessage))[0]
                            if (message.body != null) {
                                MessageRepository.getMessageById(message.stanzaId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        adapterMessages.add(GenericMessage(it))
                                    }, {
                                        if (it is NotFoundException) {
                                            val senderJid =
                                                message.from.asBareJid().asUnescapedString()
                                            ChatUserRepository.getUserAsSingle(message.from.asBareJid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe({ chatUser ->
                                                    val messageEntity = MessageEntity(
                                                        messageUid = UUID.randomUUID().toString(),
                                                        stanzaId = message.stanzaId,
                                                        timestamp = forwardedMessage.delayInformation.stamp.time,
                                                        text = message.body,
                                                        isSent = true,
                                                        isRead = true,
                                                        isCurrentUserSender = senderJid == MainApplication.getXmppConnection().jid.asUnescapedString()
                                                    )
                                                    messageEntity.chat.target = chat
                                                    messageEntity.sender.target = chatUser

                                                    MessageRepository.saveMessage(messageEntity)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe({
                                                            adapterMessages.add(GenericMessage(messageEntity))
                                                        },{ throwable -> Log.e(throwable.message) })
                                                    //need save message
                                                }, {
                                                    val chatUser = ChatUser(
                                                        jid = message.from.asBareJid().asUnescapedString(),
                                                        name = message.from.asBareJid().asUnescapedString().split(
                                                            "@"
                                                        )[0]
                                                    )
                                                    ChatUserRepository.saveUser(chatUser)
                                                        .subscribe {
                                                            val messageEntity = MessageEntity(
                                                                messageUid = UUID.randomUUID().toString(),
                                                                stanzaId = message.stanzaId,
                                                                timestamp = forwardedMessage.delayInformation.stamp.time,
                                                                text = message.body,
                                                                isSent = true,
                                                                isRead = true,
                                                                isCurrentUserSender = senderJid == MainApplication.getXmppConnection().jid.asUnescapedString()
                                                            )
                                                            messageEntity.chat.target = chat
                                                            messageEntity.sender.target = chatUser

                                                            MessageRepository.saveMessage(messageEntity)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe({
                                                                    adapterMessages.add(GenericMessage(messageEntity))
                                                                },{ throwable -> Log.e(throwable.message) })
                                                        }
                                                })

                                        }
                                    })
                            }
                        }
                    MainApplication.getMainUIThread().post {
                        adapterMessages.sortWith(messageComparator)
                        viewState?.addToEnd(adapterMessages, true)
                    }
                }
            }, {
                // FIXME
            })
    }

    @SuppressLint("CheckResult")
    fun loadLocalMessages() {
        loadLocalMessagesLogic().subscribe({ messages ->
            val genericMessages = ArrayList<GenericMessage>()
            if (messages.isNotEmpty()) {
                messages.forEach {
                    genericMessages.add(GenericMessage(it))
                }
            }
            genericMessages.sortWith(messageComparator)
            viewState.setMessages(genericMessages, true)
            viewState?.hideProgressBar()
        }, {
            com.orhanobut.logger.Logger.d(it.message)
        })
    }

    private fun loadLocalMessagesLogic(): Observable<List<MessageEntity>> {
        return MessageRepository.getMessagesByJid(JidCreate.bareFrom(chat.jid))
    }

    // FIXME
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
//mamManager.queryArchive(MamManager.MamQueryArgs.builder().limitResultsToJid(JidCreate.from(chatID)).build()).messageCount
    @SuppressLint("CheckResult")
    fun loadMessagesFromMAM(): Single<MamManager.MamQuery> {
        return Single.create {
            if (MainApplication.getXmppConnection() != null) {
                val mamManager: MamManager? = MainApplication.getXmppConnection().mamManager
                if (mamManager != null) {
                    MessageRepository.getFirstMessage(chat.jid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ msg ->
                            val mamQuery =  mamManager.queryArchive(
                                MamManager.MamQueryArgs.builder()
                                    .beforeUid(msg.messageUid)
                                    .limitResultsToJid(JidCreate.from(chatID))
                                    .setResultPageSizeTo(50)
                                    .build())
                            it.onSuccess(
                                mamQuery
                            )
                        }, { ex ->
                            it.onError(ex)
                        })
                } else {
                    it.onError(Exception())
                }
            } else {
                it.onError(Exception())
            }
        }
    }
}