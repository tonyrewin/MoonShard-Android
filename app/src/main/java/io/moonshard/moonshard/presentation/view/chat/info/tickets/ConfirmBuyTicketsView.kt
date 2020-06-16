package io.moonshard.moonshard.presentation.view.chat.info.tickets

import com.example.moonshardwallet.models.MyTicketSale
import moxy.MvpView

interface ConfirmBuyTicketsView:MvpView {
    fun setTickets(ticketSales: ArrayList<MyTicketSale>)
    fun showCost(value: String)
    fun showAmount(value: String)
    fun showToast(text: String)
    fun back()
    fun showProgressBar()
    fun hideProgressBar()
    fun showEventInfo(name: String, startDateEvent: String, address: String)
    fun showSuccessScreen(costAll: String?)
}