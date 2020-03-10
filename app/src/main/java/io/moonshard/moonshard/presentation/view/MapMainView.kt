package io.moonshard.moonshard.presentation.view

import android.graphics.Bitmap
import io.moonshard.moonshard.models.api.RoomPin
import moxy.MvpView

interface MapMainView: MvpView {
    fun showRoomsOnMap(rooms:ArrayList<RoomPin>)
    fun showError(error:String)
    fun showChatScreens(chatId: String,stateChat:String)
    fun hideJoinButtonsBottomSheet()
    fun showOnlineUserRoomInfo(onlineUser: String)
    fun showEventName(name: String)
    fun showDistance(distance: String)
    fun showDescriptionEvent(description: String)
    fun showAvatar(avatar: Bitmap)
}