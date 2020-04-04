package io.moonshard.moonshard.presentation.view.profile

import android.graphics.Bitmap
import moxy.MvpView

interface ProfileView: MvpView {
    fun setData(nickName: String?, description: String?,jid:String?)
    fun setAvatar(avatar:Bitmap?)
    fun showError(error:String)
}