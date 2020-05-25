package io.moonshard.moonshard.presentation.view

import android.graphics.Bitmap
import moxy.MvpView

interface SettingsView : MvpView {
    fun showRegistrationScreen()
    fun showError(error:String)
}