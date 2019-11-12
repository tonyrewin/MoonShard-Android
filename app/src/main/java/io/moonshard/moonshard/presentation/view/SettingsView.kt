package io.moonshard.moonshard.presentation.view

import moxy.MvpView

interface SettingsView : MvpView {
    fun showRegistrationScreen()
    fun showError(error:String)
}