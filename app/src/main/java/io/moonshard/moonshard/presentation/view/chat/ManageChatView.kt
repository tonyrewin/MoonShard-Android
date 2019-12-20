package io.moonshard.moonshard.presentation.view.chat

import android.graphics.Bitmap
import moxy.MvpView

interface ManageChatView: MvpView {
    fun setAvatar(avatar: Bitmap?)
}