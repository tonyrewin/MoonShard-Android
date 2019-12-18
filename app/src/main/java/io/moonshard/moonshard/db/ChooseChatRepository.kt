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


    fun getTimeSec():Int{
        var ttl = 60*60*24
        when (time) {
            "1 день" -> ttl = 60*60*24
            "2 дня" -> ttl = 60*60*48
            "3 дня" -> ttl = 60*60*(24*3)
            "4 дня" -> ttl = 60*60*(24*4)
            "5 дней" -> ttl = 60*60*(24*5)
            "6 дней" -> ttl = 60*60*(24*6)
            "Неделя" -> ttl = 60*60*(24*7)
        }
        return ttl
    }
}