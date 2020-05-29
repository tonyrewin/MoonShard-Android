package io.moonshard.moonshard.presentation.presenter.profile.mytickets

import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class MyTicketsPresenter : MvpPresenter<MyTicketsView>() {

    fun getMyTickets() {
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