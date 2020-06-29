package io.moonshard.moonshard.presentation.view.profile.present_ticket

import com.example.moonshardwallet.models.Ticket
import moxy.MvpView

interface PresentTicketView: MvpView {
    fun setTickets(ticketSales: ArrayList<Ticket>)
    fun showProgressBar()
    fun hideProgressBar()
    fun showToast(text:String)
}