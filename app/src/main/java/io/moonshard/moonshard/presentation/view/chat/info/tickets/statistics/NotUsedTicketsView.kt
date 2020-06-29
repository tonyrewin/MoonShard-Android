package io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics

import com.example.moonshardwallet.models.TicketSaleStatistic
import moxy.MvpView
import java.util.ArrayList

interface NotUsedTicketsView:MvpView {
    fun setNotUsedTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>)
    fun showAllNotUsedStatistic(notUsed: String, allSold: String)
}