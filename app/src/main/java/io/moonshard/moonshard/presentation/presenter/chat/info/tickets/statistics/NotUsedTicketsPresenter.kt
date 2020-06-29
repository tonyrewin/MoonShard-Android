package io.moonshard.moonshard.presentation.presenter.chat.info.tickets.statistics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.info.tickets.statistics.NotUsedTicketsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import trikita.log.Log

@InjectViewState
class NotUsedTicketsPresenter: MvpPresenter<NotUsedTicketsView>() {

    fun getNotUsedStatistic(jid:String){
        MainService.getBuyTicketService().getNotUsedStatisticTickets(jid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ saleStatistic ->
                viewState?.setNotUsedTicketsData(saleStatistic)
            }, { throwable -> Log.e(throwable.message) })
    }

    fun getNotUsedStatisticValue(jid:String){
        val allSold =   MainService.getBuyTicketService().getAllSold(jid)
        val allScanned = MainService.getBuyTicketService().getAllScannedTickets(jid)
        val notUsed = allSold.toInt() - allScanned.toInt()
        viewState?.showAllNotUsedStatistic(notUsed.toString(),allScanned)
    }
}