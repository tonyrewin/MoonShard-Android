package io.moonshard.moonshard.presentation.view.settings

import android.graphics.Bitmap
import moxy.MvpView

interface ProfileView: MvpView {
    fun setData(nickName: String?, description: String?)
    fun setAvatar(avatar:Bitmap?)
    fun showError(error:String)
}