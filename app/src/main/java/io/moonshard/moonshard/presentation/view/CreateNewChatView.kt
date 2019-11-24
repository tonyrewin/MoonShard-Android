package io.moonshard.moonshard.presentation.view

import moxy.MvpView

interface CreateNewChatView: MvpView {
    fun showToast(text: String)
    fun showMapScreen()
}