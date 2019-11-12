package io.moonshard.moonshard.presentation.presenter


import io.moonshard.moonshard.helpers.AppHelper
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.presentation.view.SettingsView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class SettingsPresenter : MvpPresenter<SettingsView>() {

    fun logOut(){
       val success =  AppHelper.getXmppConnection().logOut()
        if(success){
            viewState?.showRegistrationScreen()
        }else{
            viewState?.showError("Error")
        }
    }

}