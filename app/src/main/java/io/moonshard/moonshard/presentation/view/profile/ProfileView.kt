package io.moonshard.moonshard.presentation.view.profile

import android.graphics.Bitmap
import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType



interface ProfileView: MvpView {
    fun setData(nickName: String?, description: String?,jid:String?)
    fun setAvatar(avatar:Bitmap?)
    fun showError(error:String)
    fun setVerification(email: String?, isActivated: Boolean?)
    fun openBrowser(url: String)
}