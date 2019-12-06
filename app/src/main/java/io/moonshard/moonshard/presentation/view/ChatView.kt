package io.moonshard.moonshard.presentation.view

import android.graphics.Bitmap
import io.moonshard.moonshard.models.GenericMessage
import moxy.MvpView

interface ChatView: MvpView {
    fun cleanMessage()
    fun addToEnd(msgs:ArrayList<GenericMessage>,reverse: Boolean)
    fun addToStart(message: GenericMessage, reverse: Boolean)
    fun setData(
        name: String,
        valueOccupants: Int,
        valueOnlineMembers: Int
    )

    fun setAvatar(avatar: Bitmap?)
}