package io.moonshard.moonshard.presentation.view.create

import moxy.MvpView

interface CreateNewChatView: MvpView {
    fun showToast(text: String)
    fun showChatScreen(chatId: String)
    fun showProgressBar()
    fun hideProgressBar()
}