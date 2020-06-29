package io.moonshard.moonshard.presentation.view.chat.info.tickets

import moxy.MvpView

interface AddNewTypeTicketView:MvpView {
    fun back()
    fun hideProgressBar()
    fun showProgressBar()
    fun showToast(text: String)
}