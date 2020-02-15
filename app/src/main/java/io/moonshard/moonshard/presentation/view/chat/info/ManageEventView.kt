package io.moonshard.moonshard.presentation.view.chat.info

import android.graphics.Bitmap
import moxy.MvpView

interface ManageEventView: MvpView {
    fun showName(name:String?)
    fun showDescription(description:String)
    fun setAvatar(avatar: Bitmap?)
    fun showChatInfo()
    fun showToast(text: String)
    fun showOccupantsCount(text: String)
    fun showAdminsCount(text: String)
    fun showProgressBar()
    fun hideProgressBar()
}