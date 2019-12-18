package io.moonshard.moonshard.presentation.view.create

import moxy.MvpView

interface CreateNewChatView: MvpView {
    fun showMapScreen()
    fun showToast(text: String)
}