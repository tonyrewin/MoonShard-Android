package io.moonshard.moonshard.presentation.view

import com.arellomobile.mvp.MvpView

interface RegisterView : MvpView {
    fun showLoader()
    fun hideLoader()
    fun showToast(text:String)
}