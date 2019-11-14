package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.ChatsHelper
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.presentation.view.ChatsView
import java9.util.concurrent.CompletableFuture
import java9.util.stream.StreamSupport
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.ArrayList

@InjectViewState
class ChatsPresenter : MvpPresenter<ChatsView>() {

    private val chatsHelper = ChatsHelper()

    fun setDialogs(){
        val dialogs = ArrayList<GenericDialog>()
        StreamSupport.stream(chatsHelper.loadLocalChats())
            .forEach { chatEntity -> dialogs.add(GenericDialog(chatEntity)) }
        StreamSupport.stream(dialogs)
            .forEach { dialog ->
                val messageEntity = LocalDBWrapper.getLastMessage(dialog.id)
                if (messageEntity != null) {
                    dialog.lastMessage = GenericMessage(messageEntity)
                }
            }

        viewState?.setData(dialogs)
        loadRemoteContactList()
    }

    fun loadRemoteContactList() {
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
}