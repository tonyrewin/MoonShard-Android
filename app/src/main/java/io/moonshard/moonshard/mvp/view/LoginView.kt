package io.moonshard.moonshard.mvp.view

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun showLoader()
    fun hideLoader()
}