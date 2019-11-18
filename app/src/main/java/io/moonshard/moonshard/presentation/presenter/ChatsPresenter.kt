package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.AppHelper
import io.moonshard.moonshard.helpers.ChatsHelper
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.models.roomEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ChatsView
import java9.util.concurrent.CompletableFuture
import java9.util.stream.StreamSupport
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*
import kotlin.collections.ArrayList


@InjectViewState
class ChatsPresenter : MvpPresenter<ChatsView>() {

    private val chatsHelper = ChatsHelper()

    private var chats:ArrayList<GenericDialog> = arrayListOf()

    fun downloadChats() {
        StreamSupport.stream(loadLocalChats())
            .forEach { chatEntity -> chats.add(GenericDialog(chatEntity)) }
        viewState?.setData(chats)
    }

    private fun loadLocalChats(): List<ChatEntity> {
        return MainApplication.getChatDB().chatDao().getAllChats()
    }

    fun setDialogs() {
        val dialogs = ArrayList<GenericDialog>()
        StreamSupport.stream(chatsHelper.loadLocalChats())
            .forEach { chatEntity -> dialogs.add(GenericDialog(chatEntity)) }
        StreamSupport.stream(dialogs)
            .forEach { dialog ->
                val messageEntity = LocalDBWrapper.getLastMessage(dialog.id)
                if (messageEntity != null) {
                   // dialog.lastMessage = GenericMessage(messageEntity)
                }
            }
        viewState?.setData(dialogs)
       // loadRemoteContactList()
    }

    fun createConference() {
        try {
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val jid =
                "ploika" + "@conference." + MainApplication.getCurrentLoginCredentials().jabberHost
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)
            muc.create(nickName).makeInstant()
            muc.join(nickName)
            viewState?.showChatScreen(jid)
        } catch (e: Exception) {
            viewState?.showError(e.message!!)
        }
    }

    /*
    private fun loadRemoteContactList() {
        CompletableFuture.supplyAsync {
            chatsHelper.remoteContacts
        }
            .thenAccept { contacts ->
                MainApplication.getMainUIThread().post {
                    if (contacts != null) {
                        StreamSupport.stream(contacts).forEach { contact ->
                            val chatID = contact.jid.asUnescapedString()
                            LocalDBWrapper.createChatEntry(
                                chatID,
                                if (contact.name == null) contact.jid.asUnescapedString().split(
                                    "@"
                                )[0] else contact.name,
                                arrayListOf()
                            )
                            val dialog = GenericDialog(LocalDBWrapper.getChatByChatID(chatID))
                            val messageEntity = LocalDBWrapper.getLastMessage(chatID)
                            if (messageEntity != null) {
                                dialog.lastMessage = GenericMessage(messageEntity)
                            }

                            //  dialogListAdapter.upsertItem(dialog)
                            //  dialogListAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }
     */
}