package io.moonshard.moonshard.presentation.presenter.profile.wallet.withdraw

import android.util.Log
import com.example.moonshardwallet.MainService
import io.moonshard.moonshard.presentation.view.profile.wallet.withdraw.WithdrawWalletView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class WithdrawWalletPresenter: MvpPresenter<WithdrawWalletView>() {

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

    fun cashOut(numberCard:String, value :String){
        MainService.getWalletService().cashOut(numberCard,"card",value.toInt())?.
        subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { tx, throwable ->
                if (throwable == null) {
                    Log.d("cashOut","success")
                    Log.d("cashOut",tx.blockHash)
                } else {
                    Log.d("cashOut",throwable.message)
                    throwable.message?.let { viewState?.showToast(it) }
                }
            }
    }
}