package io.moonshard.moonshard.db

import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import java.text.SimpleDateFormat
import java.util.*

object ChooseChatRepository {
    var lat:Double?=null
    var lng:Double?=null
    var address:String = ""
    var durationTime:String = ""
    var category:Category? = null
    var name:String = ""
    var startDate: Calendar?=null
    var group: ChatEntity?=null

    fun clean(){
        address=""
        durationTime = ""
        category=null
        name=""
        lat= null
        lng= null
        startDate=null
        group = null
    }

    fun getEventStartDate(): String {
        val calendarDate = startDate?.time

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        return sdf.format(calendarDate)
       //return date!!.timeInMillis / 1000
    }

}