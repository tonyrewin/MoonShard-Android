package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.models.wallet.QrCodeModel
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ScanQrTicketView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ScanQrTicketPresenter : MvpPresenter<ScanQrTicketView>() {

    fun scanQrCode(
        ticket: QrCodeModel){
        MainService.getBuyTicketService().scanQrCode(ticket.ticketId,ticket.addressWallet,ticket.ticketSaleAddress)
        viewState?.showSuccessScannedTicket(ticket)
    }
}