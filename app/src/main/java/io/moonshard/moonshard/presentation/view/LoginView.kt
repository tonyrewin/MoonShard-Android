package io.moonshard.moonshard.presentation.view

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun showLoader()
    fun hideLoader()
    fun showContactsScreen()
    fun showError(error:String)
    fun test()
}