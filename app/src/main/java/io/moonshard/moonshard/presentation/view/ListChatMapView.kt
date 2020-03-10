package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.api.RoomPin
import moxy.MvpView

interface ListChatMapView: MvpView {
    fun setChats(chats:ArrayList<RoomPin>)
    fun onDataChange()
}