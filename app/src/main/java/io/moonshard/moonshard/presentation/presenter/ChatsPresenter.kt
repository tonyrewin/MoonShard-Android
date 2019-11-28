package io.moonshard.moonshard.presentation.presenter

import androidx.lifecycle.LiveData
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ChatsView
import moxy.InjectViewState
import moxy.MvpPresenter


@InjectViewState
class ChatsPresenter : MvpPresenter<ChatsView>() {
    private var chats: ArrayList<GenericDialog> = arrayListOf()

    private fun loadLocalChats(): LiveData<List<ChatEntity>>? {
        //return MainApplication.getChatDB().chatDao().getAllChats()
        return null
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