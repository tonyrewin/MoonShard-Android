package io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.NotUsedTicketsView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class NotUsedTicketsPresenter: MvpPresenter<NotUsedTicketsView>() {

    fun getNotUsedStatistic(jid:String){
        val saleStatistic =  MainService.getBuyTicketService().getNotUsedStatisticTickets(jid)
        viewState?.setNotUsedTicketsData(saleStatistic)
    }

    fun getNotUsedStatisticValue(jid:String){
        val allSold =   MainService.getBuyTicketService().getAllSold(jid)
        val allScanned = MainService.getBuyTicketService().getAllScannedTickets(jid)
        val notUsed = allSold.toInt() - allScanned.toInt()
        viewState?.showAllNotUsedStatistic(notUsed.toString(),allScanned)
    }
}