package io.moonshard.moonshard.presentation.view.profile.my_tickets

import com.example.moonshardwallet.models.MyTicketSale
import com.example.moonshardwallet.models.Ticket
import moxy.MvpView

interface MyTicketsView:MvpView {
    fun setTickets(ticketSales: ArrayList<Ticket>)
    fun showProgressBar()
    fun hideProgressBar()
    fun showToast(text:String)
}