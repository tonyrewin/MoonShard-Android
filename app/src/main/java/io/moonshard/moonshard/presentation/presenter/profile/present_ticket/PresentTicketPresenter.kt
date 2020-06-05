package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.profile.present_ticket.PresentTicketView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class PresentTicketPresenter : MvpPresenter<PresentTicketView>() {

    fun getMyTickets(){
        viewState?.showProgressBar()
        MainService.getBuyTicketService().myTickets
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tickets ->
                val newTickets =  tickets.filter {
                    it.payState.toInt()!=2
                }

                viewState.setTickets(ArrayList(newTickets))
                viewState?.hideProgressBar()
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
                Logger.d(it)
            })
    }
}