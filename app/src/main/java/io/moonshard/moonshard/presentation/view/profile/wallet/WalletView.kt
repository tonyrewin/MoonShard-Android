package io.moonshard.moonshard.presentation.view.profile.wallet

import moxy.MvpView

interface WalletView: MvpView {
    fun showBalance(balance: String)
    fun showToast(text: String)
}