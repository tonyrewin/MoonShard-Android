package io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.ScannedTicketView
import moxy.InjectViewState
import moxy.MvpPresenter


@InjectViewState
class ScannedTicketPresenter: MvpPresenter<ScannedTicketView>()  {

    fun getScannedStatistic(jid:String){
        val saleStatistic =  MainService.getBuyTicketService().getScannedStatisticTickets(jid)
        viewState?.setScannedTicketsData(saleStatistic)
    }

    fun getAllScanned(jid: String){
        val allSold =   MainService.getBuyTicketService().getAllSold(jid)
        val allScanned = MainService.getBuyTicketService().getAllScannedTickets(jid)
        viewState?.showAllScannedStatistic(allScanned,allSold)
    }

}