package io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics

import com.example.moonshardwallet.models.TicketSaleStatistic
import moxy.MvpView
import java.util.ArrayList

interface ScannedTicketView: MvpView {
    fun setScannedTicketsData(saleStatistic: ArrayList<TicketSaleStatistic>)
    fun showAllScannedStatistic(allScanned: String, allSold: String)
}