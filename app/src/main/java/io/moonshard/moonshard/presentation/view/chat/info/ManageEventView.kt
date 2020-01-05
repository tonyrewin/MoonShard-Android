package io.moonshard.moonshard.presentation.view.chat.info

import android.graphics.Bitmap
import moxy.MvpView

interface ManageEventView: MvpView {
    fun setAvatar(avatar: Bitmap?)
}