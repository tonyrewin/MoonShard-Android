package io.moonshard.moonshard.db

object ChatRepository {
    var idChatCurrent:String?=null
    var stateChat:String?=null

    fun clean(){
        idChatCurrent=null
        stateChat=null
    }
}