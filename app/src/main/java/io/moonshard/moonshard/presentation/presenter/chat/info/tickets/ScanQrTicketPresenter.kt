package io.moonshard.moonshard.presentation.presenter.chat.info.tickets

import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.models.wallet.QrCodeModel
import io.moonshard.moonshard.presentation.view.chat.info.tickets.ScanQrTicketView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ScanQrTicketPresenter : MvpPresenter<ScanQrTicketView>() {

    fun scanQrCode(ticket: QrCodeModel){
        //делать это действие если билет отсканирован
        MainService.getBuyTicketService().scanQrCodeRx(ticket.ticketId,ticket.addressWallet,ticket.ticketSaleAddress)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.showSuccessScannedTicket(ticket)
                Log.d("scanQrCode", "transactionReceipt: " + it.blockHash)
            }, {
                Logger.d(it)
            })
    }
}