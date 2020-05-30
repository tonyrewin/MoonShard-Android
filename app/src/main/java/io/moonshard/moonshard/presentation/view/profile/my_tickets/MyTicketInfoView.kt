package io.moonshard.moonshard.presentation.view.profile.my_tickets

import android.graphics.Bitmap
import moxy.MvpView

interface MyTicketInfoView: MvpView {
    fun showEventInfo(name: String)
    fun setAvatar(avatar: Bitmap?)
}