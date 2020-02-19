package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.ChatListItem
import io.moonshard.moonshard.models.GenericDialog
import moxy.MvpView
import java.util.ArrayList

interface ChatsView: MvpView {
    fun showError(error: String)
    fun showChatScreen(chatId: String)
    fun updateChatList(chats: List<ChatListItem>)
}