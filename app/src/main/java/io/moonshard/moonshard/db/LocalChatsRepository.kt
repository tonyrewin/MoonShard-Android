package io.moonshard.moonshard.db

import io.moonshard.moonshard.models.LocalChat

object LocalChatsRepository {
    private val localChats = HashMap<String, LocalChat>()

    fun getAllLocalChats(): Map<String, LocalChat> {
        return localChats
    }

    fun addLocalChat(topic: String, localChat: LocalChat) {
        localChats[topic] = localChat
    }

    fun removeLocalChat(topic: String) {
        localChats.remove(topic)
    }

    fun getLocalChat(topic: String): LocalChat? {
        return localChats[topic]
    }
}