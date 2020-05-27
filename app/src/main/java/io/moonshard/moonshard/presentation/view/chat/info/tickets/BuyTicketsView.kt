package io.moonshard.moonshard.presentation.view.chat.info.tickets

import com.example.moonshardwallet.models.MyTicket
import moxy.MvpView

interface BuyTicketsView:MvpView {
    fun setTickets(tickets: ArrayList<MyTicket>)
}