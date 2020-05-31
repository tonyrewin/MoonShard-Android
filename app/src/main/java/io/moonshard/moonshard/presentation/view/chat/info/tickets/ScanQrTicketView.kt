package io.moonshard.moonshard.presentation.view.chat.info.tickets

import com.example.moonshardwallet.models.Ticket
import io.moonshard.moonshard.models.wallet.QrCodeModel
import moxy.MvpView

interface ScanQrTicketView:MvpView {
    fun showSuccessScannedTicket(ticket: QrCodeModel)
}