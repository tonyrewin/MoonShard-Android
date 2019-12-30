package io.moonshard.moonshard.presentation.view.chat.info

import android.graphics.Bitmap
import moxy.MvpView

interface ProfileUserView: MvpView {
    fun setData(nickName: String?, description: String?)
    fun setAvatar(avatar: Bitmap?)
    fun showError(error: String)
    fun showChatScreen(chatId: String)
}