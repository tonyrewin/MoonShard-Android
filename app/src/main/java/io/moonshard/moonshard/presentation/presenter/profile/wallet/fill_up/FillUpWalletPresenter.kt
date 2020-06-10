package io.moonshard.moonshard.presentation.presenter.profile.wallet.fill_up

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.profile.wallet.fill_up.FillUpWalletView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class FillUpWalletPresenter: MvpPresenter<FillUpWalletView>() {

    fun getBalance() {
        MainService.getWalletService().balance?.
        subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { balance, throwable ->
                if (throwable == null) {
                    viewState?.showBalance(balance)
                } else {
                    throwable.message?.let { viewState?.showToast(it) }
                }
            }
    }
}