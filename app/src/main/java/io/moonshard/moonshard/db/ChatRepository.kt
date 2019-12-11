package io.moonshard.moonshard.db

object ChatRepository {
    var idChatCurrent:String?=null

    fun clean(){
        idChatCurrent=null
    }
}