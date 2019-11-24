package io.moonshard.moonshard.presentation.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.ChatsHelper
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.roomEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ChatsView
import java9.util.stream.StreamSupport
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart


@InjectViewState
class ChatsPresenter : MvpPresenter<ChatsView>() {

    private val chatsHelper = ChatsHelper()

    private var chats: ArrayList<GenericDialog> = arrayListOf()

    private fun loadLocalChats(): LiveData<List<ChatEntity>> {
        return MainApplication.getChatDB().chatDao().getAllChats()
    }

    fun setDialogs() {
        /*val dialogs = ArrayList<GenericDialog>()
        StreamSupport.stream(chatsHelper.loadLocalChats())
            .forEach { chatEntity -> dialogs.add(GenericDialog(chatEntity)) }
        StreamSupport.stream(dialogs)
            .forEach { dialog ->
                val messageEntity = LocalDBWrapper.getLastMessage(dialog.id)
                if (messageEntity != null) {
                   // dialog.lastMessage = GenericMessage(messageEntity)
                }
            }

        chatsHelper.loadLocalChats().observe(this, Observer<List<ChatEntity>>())
        viewState?.setData(dialogs)*/
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