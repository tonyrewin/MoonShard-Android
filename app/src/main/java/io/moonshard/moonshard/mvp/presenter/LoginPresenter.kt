package io.moonshard.moonshard.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.moonshard.moonshard.mvp.view.LoginView

@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

    fun showLoader() {
        viewState?.showLoader()
    }
}