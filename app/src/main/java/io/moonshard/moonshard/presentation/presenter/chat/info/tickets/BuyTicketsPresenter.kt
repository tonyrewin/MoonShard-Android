package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.chat.info.tickets.BuyTicketsView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class BuyTicketsPresenter: MvpPresenter<BuyTicketsView>() {

    fun getTypesTicket(eventJid:String){
        val typesTicket =  MainService.getBuyTicketSErvice().getTickets(eventJid)
        viewState.setTickets(typesTicket)
        var test = ""
    }

    fun buyTicket(saleAddress:String,amount:Int){
        MainService.getBuyTicketSErvice().buy(saleAddress,amount).thenAccept {
          // var events_tx = sale_instance.getTokensPurchasedEvents(transactionReceipt);
            // do somthing with event response
            var event_tx_ticket = MainService.getBuyTicketSErvice().ticket.getTicketBoughtHumanEvents(it);
            var test = ""

          var tickets =   MainService.getBuyTicketSErvice().myTicketsByOwner
        }.exceptionally { e ->
            null
        }
    }
}