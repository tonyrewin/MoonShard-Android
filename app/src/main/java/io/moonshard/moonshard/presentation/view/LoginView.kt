package io.moonshard.moonshard.presentation.view

import moxy.MvpView

interface LoginView : MvpView {
    fun showLoader()
    fun hideLoader()
    fun showContactsScreen()
    fun showError(error: String)
    fun startService()
}