package io.moonshard.moonshard.presentation.view.chat.info.tickets

import io.moonshard.moonshard.models.wallet.QrCodeModel
import moxy.MvpView

interface ScanQrTicketView:MvpView {
    fun showSuccessScannedTicket(
        ticket: QrCodeModel,
        typeTicketName: String
    )
    fun alreadyWasScan(error: String, typeTicketName: String)
    fun showProgressBar()
    fun hideProgressBar()
}