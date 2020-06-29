package io.moonshard.moonshard.presentation.view.profile.wallet.withdraw

import moxy.MvpView

interface WithdrawWalletView: MvpView {
    fun showBalance(balance: String)
    fun showToast(text: String)
    fun showSuccessScreen()
    fun hideProgressBar()
    fun showProgressBar()
}