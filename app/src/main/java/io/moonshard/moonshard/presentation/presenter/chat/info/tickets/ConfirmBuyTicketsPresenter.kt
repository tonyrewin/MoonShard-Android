package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.MyTicketSale
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ConfirmBuyTicketsView
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket.BuyTicketObject
import moxy.InjectViewState
import moxy.MvpPresenter
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.util.concurrent.CompletableFuture

@InjectViewState
class ConfirmBuyTicketsPresenter: MvpPresenter<ConfirmBuyTicketsView>() {

    fun getConfirmTickets(){
        val ticketSales = BuyTicketObject.ticketSales


        val ticketsConfirm = arrayListOf<MyTicketSale>()
        for ((key, value) in ticketSales) {
            for(i in 0 until value){
                ticketsConfirm.add(key)
            }
        }
        getCostAllTicket()
        getAmountAllTicket()

        viewState?.setTickets(ticketsConfirm)
    }

    private fun  getCostAllTicket() {
        var costAll: Double = 0.0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            var cost = ticketSaleValue * key.key.priceTicket.toDouble()
            costAll = costAll+cost
        }
        viewState?.showCost(costAll.toInt().toString())
    }

   private fun  getAmountAllTicket() {
        var amountAll: Int = 0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            amountAll = amountAll + ticketSaleValue
        }
        viewState?.showAmount(amountAll.toString())
    }

    fun buyTickets() {
        BuyTicketObject.ticketSales.forEach { (key, value) ->
            Log.d("eventTxTicket key: ",value.toString())
            Log.d("eventTxTicket originSaleAddress: ",key.originSaleAddress.toString())


           val future =  MainService.getBuyTicketService().buy(key.originSaleAddress, value).thenAcceptAsync {
                // var events_tx = sale_instance.getTokensPurchasedEvents(transactionReceipt);
                // do somthing with event response
                var event_tx_ticket =
                    MainService.getBuyTicketService().ticket.getTicketBoughtHumanEvents(it)

                Log.d("eventTxTicket transaction ","test")

            }.exceptionally { e ->
                Log.d("eventTxTicket error: ",e.message)
            //    e.message?.let { viewState?.showToast(it) }
                Logger.d(e)
                null
            }
            future.get()
        }
          viewState?.showToast("Оплата успешно выполнена")
          BuyTicketObject.ticketSales.clear() //временно
    }
}