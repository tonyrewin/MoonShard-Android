package io.moonshard.moonshard.presentation.presenter


import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.AppHelper
import io.moonshard.moonshard.presentation.view.RegisterView
import moxy.InjectViewState
import moxy.MvpPresenter


@InjectViewState
class RegisterPresenter : MvpPresenter<RegisterView>() {

    fun register(email: String, pass: String) {
        viewState.showLoader()
        MainApplication.getXmppConnection()?.register(email, pass)
    }
}