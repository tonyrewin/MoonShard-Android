package io.moonshard.moonshard.presentation.view

import moxy.MvpView

interface RegisterView : MvpView {
    fun showLoader()
    fun hideLoader()
    fun showToast(text:String)
    fun showContactsScreen()
    fun showError(error: String)
    fun showStartProfileScreen()
    fun startService()
    fun setRegisterLayout()
}