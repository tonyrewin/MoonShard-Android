package io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics

import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.TicketSaleStatistic
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.SalesStatisticTicketView
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.ArrayList

@InjectViewState
class SalesStatisticTicketPresenter: MvpPresenter<SalesStatisticTicketView>() {

    fun getSalesStatistic(jid:String){
         val saleStatistic =  MainService.getBuyTicketService().getSoldStaticsTickets(jid)

        viewState?.setSalesTicketsData(saleStatistic)
        viewState?.setBalanceTicketsData(saleStatistic)
    }

     fun getAllSaleStatistic(jid:String){
         val allSold =   MainService.getBuyTicketService().getAllSold(jid)
         val allSaleLimit = MainService.getBuyTicketService().getAllSaleLimitInTicketsSale(jid)


         val balance = allSaleLimit.toInt()-allSold.toInt()
         viewState?.showBalanceStatisticData(balance.toString())

         viewState?.showSaleStatisticData(allSold,allSaleLimit)
     }
}