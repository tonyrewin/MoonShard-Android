package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.api.RoomPin
import moxy.MvpView

interface MapMainView: MvpView {
    fun showRoomsOnMap(rooms:ArrayList<RoomPin>)
    fun showError(error:String)
    fun showChatScreens(chatId: String)
}