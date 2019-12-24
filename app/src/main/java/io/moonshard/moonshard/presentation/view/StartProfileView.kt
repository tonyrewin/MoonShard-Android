package io.moonshard.moonshard.presentation.view

import android.graphics.Bitmap
import moxy.MvpView

interface StartProfileView: MvpView {
    fun setAvatar(avatar: Bitmap?)
    fun showError(error: String)
    fun showContactsScreen()
}