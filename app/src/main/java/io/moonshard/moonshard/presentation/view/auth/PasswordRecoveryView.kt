package io.moonshard.moonshard.presentation.view.auth

import moxy.MvpView

interface PasswordRecoveryView:MvpView {
    fun showProgressBar()
    fun hideProgressBar()
    fun showError(error: String)
}