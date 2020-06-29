package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.models.wallet.QrCodeModel
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ScanQrTicketView
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ScanQrTicketPresenter : MvpPresenter<ScanQrTicketView>() {

    private var eventsUseCase: EventsUseCase? = null

    init {
        eventsUseCase = EventsUseCase()
    }


    fun scan(ticket: QrCodeModel) {
        viewState?.showProgressBar()

        Log.d("getTicketTypeName", "$ticket.jidEvent, $ticket.ticketType.toInt()")
        eventsUseCase!!.getTicketTypeName(ticket.jidEvent, ticket.ticketType.toInt())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { ticketTypeName, throwable ->
                if (throwable == null) {
                    Log.d("typeTicketName",ticketTypeName.typeName)
                    scanQrCode(ticket, ticketTypeName.typeName)
                } else {
                    throwable.message?.let { Logger.e(throwable.message!!) }
                    viewState?.alreadyWasScan("Произошла ошибка", "")
                    viewState?.hideProgressBar()
                }
            }
    }

    fun scanQrCode(ticket: QrCodeModel, typeTicketName: String) {
        when {
            ticket.ticketPayState.toInt() == 0 -> {
                viewState?.alreadyWasScan("Билет не существует", typeTicketName)
                viewState?.hideProgressBar()
            }
            ticket.ticketPayState.toInt() == 1 -> {
                //делать это действие если билет отсканирован
                MainService.getBuyTicketService()
                    .scanQrCodeRx(ticket.ticketId, ticket.addressWallet, ticket.ticketSaleAddress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //   val event_tx: List<Ticket721.TicketFulfilledHumanEventResponse> = MainService.getBuyTicketService().ticket.getTicketFulfilledHumanEvents(it)
                        viewState?.showSuccessScannedTicket(ticket,typeTicketName)
                        viewState?.hideProgressBar()
                        Log.d("scanQrCode", "transactionReceipt: " + it.blockHash)
                    }, {
                        viewState?.alreadyWasScan("Произошла ошибка", typeTicketName)
                        viewState?.hideProgressBar()
                        it.message?.let { it1 -> Logger.e(it1) }
                    })
            }
            ticket.ticketPayState.toInt() == 2 -> {
                viewState?.alreadyWasScan("Билет уже отсканирован", typeTicketName)
                viewState?.hideProgressBar()
            }
            ticket.ticketPayState.toInt() == 3 -> {
                viewState?.alreadyWasScan("Отменен", typeTicketName)
                viewState?.hideProgressBar()
            }
        }
    }
}