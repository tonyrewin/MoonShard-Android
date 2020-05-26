package io.moonshard.moonshard.presentation.view.chat.info.tickets

import com.example.moonshardwallet.models.MyTicket
import moxy.MvpView

interface ManageTypesTicketView:MvpView {
    fun setTypesTicket(tickets: ArrayList<MyTicket>)
}