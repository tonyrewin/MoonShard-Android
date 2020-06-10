package io.moonshard.moonshard.presentation.view.profile.wallet.fill_up

import moxy.MvpView

interface FillUpWalletView: MvpView {
    fun showBalance(balance: String)
    fun showToast(text: String)
}