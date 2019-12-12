package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.GenericDialog
import moxy.MvpView
import java.util.ArrayList

interface ChatsView: MvpView {
    //fun setData(chats: ArrayList<GenericDialog>)
    fun showError(error:String)
    fun showChatScreen(chatId:String,chatName:String)
    fun addNewChat(chat:GenericDialog)
}