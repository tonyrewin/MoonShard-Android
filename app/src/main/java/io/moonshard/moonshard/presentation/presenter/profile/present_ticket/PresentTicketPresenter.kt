package io.moonshard.moonshard.presentation.presenter.profile.present_ticket

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
        MainService.getBuyTicketSErvice().myTickets
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setTickets(it)
                viewState?.hideProgressBar()
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
                Logger.d(it)
            })
    }
}