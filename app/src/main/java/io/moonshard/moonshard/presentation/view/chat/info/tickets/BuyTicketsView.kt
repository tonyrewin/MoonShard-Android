package io.moonshard.moonshard.presentation.view.chat.info.tickets

import android.graphics.Bitmap
import com.example.moonshardwallet.models.MyTicketSale
import moxy.MvpView

interface BuyTicketsView:MvpView {
    fun setTickets(ticketSales: ArrayList<MyTicketSale>)
    fun showCost(value: String)
    fun showAmount(value: String)

    fun showProgressBar()
    fun hideProgressBar()

    fun showNameEvent(name:String?)
    fun showStartDateEvent(date:String?)
    fun showAvatarEvent(avatar: Bitmap)
}