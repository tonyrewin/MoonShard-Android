package io.moonshard.moonshard.presentation.view.profile.my_tickets

import android.graphics.Bitmap
import moxy.MvpView

interface MyTicketInfoView: MvpView {
    fun showEventInfo(name: String, startDateEvent:String, address:String)
    fun setAvatar(avatar: Bitmap?)
    fun showProgressBar()
    fun hideProgressBar()
    fun showToast(text:String)
}