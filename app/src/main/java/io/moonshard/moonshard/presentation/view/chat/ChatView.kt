package io.moonshard.moonshard.presentation.view.chat

import android.graphics.Bitmap
import moxy.MvpView

interface ChatView: MvpView {
    fun setDataMuc(
        name: String,
        valueOccupants: Int,
        valueOnlineMembers: Int
    )

    fun setNameUser(name:String)

    fun setAvatar(avatar: Bitmap?)

    fun showError(error:String)
    fun initViewPager()
    fun initViewPagerFromEvent()
}