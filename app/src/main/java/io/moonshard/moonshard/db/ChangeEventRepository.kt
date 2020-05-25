package io.moonshard.moonshard.db

import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import java.text.SimpleDateFormat
import java.util.*

object ChangeEventRepository {
    var event:RoomPin?=null
    var name: String = ""
    var address: String = ""
    var description: String = ""

    fun clean() {
        event = null
        address = ""
        name = ""
        description = ""
    }

    private fun convertCalendarToTimeStamp(calendar:Calendar): Long {
        return calendar.timeInMillis / 1000
    }

    fun setStartDate(calendar: Calendar){
        event!!.eventStartDate = convertCalendarToTimeStamp(calendar)
    }
}