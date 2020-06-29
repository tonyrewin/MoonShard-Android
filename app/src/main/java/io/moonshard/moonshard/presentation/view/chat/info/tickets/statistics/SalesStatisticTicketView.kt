package io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics

import com.example.moonshardwallet.models.TicketSaleStatistic
import moxy.MvpView
import java.util.ArrayList

interface SalesStatisticTicketView:MvpView {
    fun setSalesTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>)
    fun setBalanceTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>)
    fun showSaleStatisticData(allSold: String, allSaleLimit: String)
    fun showBalanceStatisticData(balance: String)
}