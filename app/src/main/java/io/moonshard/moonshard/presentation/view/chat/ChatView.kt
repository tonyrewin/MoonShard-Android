package io.moonshard.moonshard.presentation.view.chat

import android.graphics.Bitmap
import moxy.MvpView

interface ChatView: MvpView {
    fun setData(
        name: String,
        valueOccupants: Int,
        valueOnlineMembers: Int
    )

    fun setAvatar(avatar: Bitmap?)

    fun showError(error:String)
}