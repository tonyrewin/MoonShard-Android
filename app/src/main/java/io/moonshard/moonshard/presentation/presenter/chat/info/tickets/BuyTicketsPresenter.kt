package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.models.MyTicketSale
import io.moonshard.moonshard.presentation.view.chat.info.tickets.BuyTicketsView
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.buyticket.BuyTicketObject
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class BuyTicketsPresenter : MvpPresenter<BuyTicketsView>() {

    fun getTypesTicket(eventJid: String) {
        val typesTicket = MainService.getBuyTicketService().getTicketsTypes(eventJid)

        for (i in typesTicket.indices) {
            BuyTicketObject.ticketSales.put(typesTicket[i], 0)
        }
        viewState.setTickets(typesTicket)
    }

    fun buyTicket(saleAddress: String, amount: Int) {
        MainService.getBuyTicketService().buy(saleAddress, amount).thenAccept {
            // var events_tx = sale_instance.getTokensPurchasedEvents(transactionReceipt);
            // do somthing with event response
            var event_tx_ticket =
                MainService.getBuyTicketService().ticket.getTicketBoughtHumanEvents(it)
            var test = ""

            var tickets = MainService.getBuyTicketService().myTicketsByOwner
        }.exceptionally { e ->
            null
        }
    }

    fun plusTicketSale(ticketSale: MyTicketSale) {
        val value = BuyTicketObject.ticketSales[ticketSale]
        val amount = value!! + 1
        BuyTicketObject.ticketSales.put(ticketSale, amount)
        viewState?.showCost(getCostAllTicket())
        viewState?.showAmount(getAmountAllTicket())
    }

    fun minusTicketSale(ticketSale: MyTicketSale) {
        val value = BuyTicketObject.ticketSales[ticketSale]
        if (value!! > 0) {
            val amount = value - 1
            BuyTicketObject.ticketSales.put(ticketSale, amount)
            viewState?.showCost(getCostAllTicket())
            viewState?.showAmount(getAmountAllTicket())
        }
    }

    private fun  getCostAllTicket():String {
        var amountAll: Double = 0.0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            var amount = ticketSaleValue * key.key.priceTicket.toDouble()
            amountAll = amountAll+amount
        }
        return amountAll.toInt().toString()
    }

    fun  getAmountAllTicket():String {
        var amountAll: Int = 0
        for (key in BuyTicketObject.ticketSales) {
            val ticketSaleValue = key.value
            amountAll = amountAll + ticketSaleValue
        }
        return amountAll.toString()
    }
}