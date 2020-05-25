package io.moonshard.moonshard.presentation.view.chat

import io.moonshard.moonshard.models.dbEntities.ChatEntity
import moxy.MvpView

interface RecommendationsView: MvpView {
    fun showRecommendations(recommendations:List<ChatEntity>)
    fun showChatScreen(chatId: String)
    fun onDataChange()
}