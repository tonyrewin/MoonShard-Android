package io.moonshard.moonshard.ui.fragments.map

import io.moonshard.moonshard.models.api.RoomPin

object RoomsMap {
    var rooms = arrayListOf<RoomPin>()


    fun clean(){
        rooms.clear()
    }
}