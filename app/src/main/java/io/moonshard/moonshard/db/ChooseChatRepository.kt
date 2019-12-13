package io.moonshard.moonshard.db

import io.moonshard.moonshard.models.api.Category
import java.util.*

object ChooseChatRepository {
    var lat:Double?=null
    var lng:Double?=null
    var address:String = ""
    var time:String = ""
    var category:Category? = null
    var name:String = ""
    var date: Calendar?=null


    fun clean(){
        address=""
        time = ""
        category=null
        name=""
        lat= null
        lng= null
        date=null
    }


    fun getTimeSec():Int{
        var ttl = 60*60*6
        when (time) {
            "6 часов" -> ttl = 60*60*6
            "12 часов" -> ttl = 60*60*12
            "24 часа" -> ttl = 60*60*24
            "3 дня" -> ttl = 60*60*(24*3)
            "1 неделю" -> ttl = 60*60*(24*7)
        }
        return ttl
    }
}