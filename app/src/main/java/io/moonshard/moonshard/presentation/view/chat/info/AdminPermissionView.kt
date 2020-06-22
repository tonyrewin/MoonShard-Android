package io.moonshard.moonshard.presentation.view.chat.info

import android.graphics.Bitmap
import moxy.MvpView

interface AdminPermissionView:MvpView {
    fun setAvatar(avatar:Bitmap)
    fun showNickName(nickName: String)
    fun goToChatScreen()
}