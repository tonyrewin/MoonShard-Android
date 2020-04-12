package io.moonshard.moonshard.db

import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import java.util.*

object ChooseChatRepository {
    var lat:Double?=null
    var lng:Double?=null
    var address:String = ""
    var time:String = ""
    var category:Category? = null
    var name:String = ""
    var date: Calendar?=null
    var group: ChatEntity?=null

    fun clean(){
        address=""
        time = ""
        category=null
        name=""
        lat= null
        lng= null
        date=null
        group = null
    }

    fun getEventStartDate(): Long {
       return date!!.timeInMillis / 1000
    }

}