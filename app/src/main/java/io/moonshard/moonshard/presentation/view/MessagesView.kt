package io.moonshard.moonshard.presentation.view

import android.graphics.Bitmap
import io.moonshard.moonshard.models.GenericMessage
import moxy.MvpView

interface MessagesView: MvpView {
    fun cleanMessage()
    fun addToEnd(msgs:ArrayList<GenericMessage>,reverse: Boolean)
    fun addToStart(message: GenericMessage, reverse: Boolean)
    fun setMessages(msgs: ArrayList<GenericMessage>, reverse: Boolean)
    fun showProgressBar()
    fun hideProgressBar()
}