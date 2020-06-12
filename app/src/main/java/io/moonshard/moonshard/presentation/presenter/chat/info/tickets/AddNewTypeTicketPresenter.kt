package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import com.example.moonshardwallet.contracts.TicketFactory721
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.chat.info.tickets.AddNewTypeTicketView
import moxy.InjectViewState
import moxy.MvpPresenter
import java.math.BigInteger

@InjectViewState
class AddNewTypeTicketPresenter : MvpPresenter<AddNewTypeTicketView>() {

    fun addNewEvent(
        typeString: String,
        price: String,
        limit: String,
        jidEvent: String
    ) {

        val sizeTicketSales = MainService.getBuyTicketService().sizeTicketSales(jidEvent)

        if (sizeTicketSales == null) {
            MainService.getBuyTicketService()
                .createTicketSale(BigInteger(price), jidEvent, BigInteger(limit))
                .thenAccept {
                    //всегда  размер массива 1
                    val event_tx: TicketFactory721.SaleCreatedHumanEventResponse? =
                        MainService.getWalletService().ticketfactory.getSaleCreatedHumanEvents(it)[0]
                    val typeInt = event_tx?.ticket_type
                    // отправлять в связке тип в стринге и тип в инте на сервер
                    viewState?.back()
                }.exceptionally { e ->
                    Logger.d(e)
                    null
                }
        } else {
            val originSale = MainService.getBuyTicketService().getEventIdFromFirstSale(jidEvent)
            MainService.getBuyTicketService()
                .createNewType(originSale, BigInteger(price), BigInteger(limit)).thenAccept {
                var event_tx_ticket =
                    MainService.getBuyTicketService().ticket.getTicketBoughtHumanEvents(it) //todo тут вернулся 0
                Log.d("plugIn tx: ", it.blockHash)
                viewState?.back()
            }.exceptionally { e ->
                null
            }
        }
    }
}