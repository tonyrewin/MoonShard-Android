package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.GenericDialog
import moxy.MvpView
import java.util.ArrayList

interface ChatsView: MvpView {
    fun setData(chats: ArrayList<GenericDialog>)
    fun showError(error:String)
}