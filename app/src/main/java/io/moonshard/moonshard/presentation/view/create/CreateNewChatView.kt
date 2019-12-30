package io.moonshard.moonshard.presentation.view.create

import moxy.MvpView

interface CreateNewChatView: MvpView {
    fun showChatsScreen()
    fun showToast(text: String)
    fun showChatScreen(chatId: String)
}