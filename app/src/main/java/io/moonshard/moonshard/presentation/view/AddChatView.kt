package io.moonshard.moonshard.presentation.view

import moxy.MvpView

interface AddChatView : MvpView {
    fun showError(text:String)
    fun back()
}