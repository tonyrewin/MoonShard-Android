package io.moonshard.moonshard.presentation.view.chat.info

import moxy.MvpView

interface InviteUserView: MvpView {
    fun showError(error:String)
    fun showChatScreen()
}