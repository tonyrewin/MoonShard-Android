package io.moonshard.moonshard.db

object ChooseChatRepository {
    var address:String = ""
    var time:String = ""
    var category:String = ""
    var name:String = ""

    fun clean(){
        address=""
        time = ""
        category=""
        name=""
    }
}