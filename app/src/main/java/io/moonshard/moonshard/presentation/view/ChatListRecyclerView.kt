package io.moonshard.moonshard.presentation.view

import moxy.MvpView

interface ChatListRecyclerView : MvpView {
    fun onDataChange()
    fun onItemChange(position: Int)
}