package io.moonshard.moonshard.presentation.view.chat.info.tickets

import com.example.moonshardwallet.models.MyTicketSale
import moxy.MvpView

interface BuyTicketsView:MvpView {
    fun setTickets(ticketSales: ArrayList<MyTicketSale>)
}