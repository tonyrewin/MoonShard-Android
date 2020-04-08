package io.moonshard.moonshard.presentation.view.profile

import android.graphics.Bitmap
import moxy.MvpView

interface ChangeProfileView: MvpView {
    fun setData(nickName: String?, description: String?)
    fun setAvatar(avatar:Bitmap?)
    fun showProfile()
    fun showError(error:String)
    fun showProgressBar()
    fun hideProgressBar()
}