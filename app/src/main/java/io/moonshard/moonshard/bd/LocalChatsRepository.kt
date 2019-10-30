package io.moonshard.moonshard.bd

import io.moonshard.moonshard.mvp.models.LocalChat

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