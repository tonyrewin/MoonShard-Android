package io.moonshard.moonshard.ui.fragments.map

import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin

object RoomsMap {
    var rooms = arrayListOf<RoomPin>()
    var isFilter:Boolean = false
    var category:Category?=null


    fun clean(){
        rooms.clear()
    }
}