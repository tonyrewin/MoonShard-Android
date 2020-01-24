package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.ChatListItem
import moxy.MvpView

interface ChatListRecyclerView : MvpView {
    fun onDataChange()
    fun onItemChange(position: Int)
    fun onItemDelete(position: Int)
    fun setData(chats: List<ChatListItem>)
}