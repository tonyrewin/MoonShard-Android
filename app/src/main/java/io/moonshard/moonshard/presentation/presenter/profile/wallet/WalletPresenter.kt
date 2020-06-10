package io.moonshard.moonshard.presentation.presenter.profile.wallet

import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.presentation.view.profile.wallet.WalletView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class WalletPresenter : MvpPresenter<WalletView>() {

    fun getBalance() {
        MainService.getWalletService()?.balance?.
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