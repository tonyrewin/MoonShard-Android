package io.moonshard.moonshard.presentation.view.settings

import moxy.MvpView

interface SecurityView: MvpView {
    fun showError(error:String)
    fun showSettingsScreen()
}