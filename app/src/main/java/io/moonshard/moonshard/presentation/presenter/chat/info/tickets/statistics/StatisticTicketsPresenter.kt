package io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.StatisticTicketsView
import moxy.InjectViewState
import moxy.MvpPresenter


@InjectViewState
class StatisticTicketsPresenter: MvpPresenter<StatisticTicketsView>()  {

    fun getSaleStatistic(jid:String){
        viewState?.showProgressBar()
      val allSold =   MainService.getBuyTicketService().getAllSold(jid)
        val allSaleLimit = MainService.getBuyTicketService().getAllSaleLimitInTicketsSale(jid)
        viewState?.showSaleStatisticData(allSold,allSaleLimit)
        getScannedStatistic(jid)
    }

    fun getScannedStatistic(jid:String){
        val allSold =   MainService.getBuyTicketService().getAllSold(jid)
        val allScanned = MainService.getBuyTicketService().getAllScannedTickets(jid)
        viewState?.showScannedStatisticData(allScanned,allSold)
        getNotUsedStatistic(jid,allSold,allScanned)
    }

    fun getNotUsedStatistic(jid:String,allSold:String,allScanned:String){
        val notUsed = allSold.toInt() - allScanned.toInt()
        viewState?.showNotUsedStatisticData(notUsed.toString(),allSold)
        viewState?.hideProgressBar()
    }
}