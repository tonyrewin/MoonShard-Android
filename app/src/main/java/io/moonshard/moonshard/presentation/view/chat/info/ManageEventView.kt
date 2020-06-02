package io.moonshard.moonshard.presentation.view.chat.info

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import moxy.MvpView

interface ManageEventView: MvpView {
    fun showName(name:String?)
    fun showDescription(description:String)
    fun setAvatar(avatar: Bitmap?)
    fun showChatInfo()
    fun showToast(text: String)
    fun showOccupantsCount(text: String)
    fun showAdminsCount(text: String)
    fun setStartDate(dayOfMonth: Int, month: Int)
    fun showTimeDays(timeDays:Long)
    fun showAdress(location:LatLng)
    fun showProgressBar()
    fun hideProgressBar()
    fun showChatsScreen()
    fun initManageTicket(isActivated: Boolean)
}