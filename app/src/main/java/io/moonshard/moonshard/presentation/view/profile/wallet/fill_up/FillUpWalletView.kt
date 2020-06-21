package io.moonshard.moonshard.presentation.view.profile.wallet.fill_up

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface FillUpWalletView: MvpView {
    fun showBalance(balance: String)
    fun showToast(text: String)

    @StateStrategyType(value = SkipStrategy::class)
        fun openBrowser(url: String)
}