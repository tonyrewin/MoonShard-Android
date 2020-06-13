package io.moonshard.moonshard.presentation.view.chat.info.tickets

import com.example.moonshardwallet.models.MyTicketSale
import moxy.MvpView

interface ManageTypesTicketView:MvpView {
    fun setTypesTicket(ticketSales: ArrayList<MyTicketSale>)
    fun showToast(text: String)
}