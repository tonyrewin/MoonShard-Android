package io.moonshard.moonshard.presentation.presenter.profile.wallet.fill_up

import android.os.Looper
import android.util.Log
import com.example.moonshardwallet.MainService
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.presentation.view.profile.wallet.fill_up.FillUpWalletView
import io.moonshard.moonshard.usecase.UnitPayUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class FillUpWalletPresenter : MvpPresenter<FillUpWalletView>() {

    private var unitPayUseCase: UnitPayUseCase? = null

    init {
        unitPayUseCase = UnitPayUseCase()
    }

    fun getBalance() {
        MainService.getWalletService().balance?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { balance, throwable ->
                if (throwable == null) {
                    viewState?.showBalance(balance)
                } else {
                    throwable.message?.let { viewState?.showToast(it) }
                }
            }
    }

    fun fillUpBalance(sum: String) {
        val walletAddress = MainService.getWalletService().myAddress
        unitPayUseCase?.createPay(sum.toInt(), walletAddress, "description test")
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { url, throwable ->
                if (throwable == null) {
                    Log.d("fillUpBalance", url)
                    viewState?.openBrowser(url)
                } else {
                    Logger.d("fillUpBalance", throwable)
                }
            }
    }
}