package io.moonshard.moonshard.db

object ChooseChatRepository {
    var lat:Float?=null
    var lng:Float?=null
    var address:String = ""
    var time:String = ""
    var category:String = ""
    var name:String = ""

    fun clean(){
        address=""
        time = ""
        category=""
        name=""
        lat= null
        lng= null
    }


    fun getTimeSec():Int{
        var ttl = 60*60*6
        when (time) {
            "6 hours" -> ttl = 60*60*6
            "12 hours" -> ttl = 60*60*12
            "24 hours" -> ttl = 60*60*24
            "3 days" -> ttl = 60*60*(24*3)
            "1 week" -> ttl = 60*60*(24*7)
        }
        return ttl
    }
}