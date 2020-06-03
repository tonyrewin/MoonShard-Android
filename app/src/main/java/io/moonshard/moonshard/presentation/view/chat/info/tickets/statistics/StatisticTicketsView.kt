package io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics

import moxy.MvpView

interface StatisticTicketsView:MvpView {
   fun showSaleStatisticData(allSold:String,allSaleLimit:String)
    fun showScannedStatisticData(allScanned: String, allSold: String)
}