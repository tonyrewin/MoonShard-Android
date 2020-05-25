package io.moonshard.moonshard.presentation.presenter.chat

import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.RecommendationsView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.disco.packet.DiscoverItems
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class RecommendationsPresenter : MvpPresenter<RecommendationsView>() {

    private var recommendations = ArrayList<ChatEntity>()
    private var fullRecommendations = ArrayList<ChatEntity>()

    fun getRecommendation() {
        try {
            val chatsAndEvents =
                MainApplication.getXmppConnection().getServiceDiscoveryManager()!!.discoverItems(
                    JidCreate.from("conference.moonshard.tech")
                )
                    .items

            val chats = chatsAndEvents.filter { it.entityID.contains("chat") }

            val chatsEntity = convertDiscoverItemsToChatEntityList(chats)
            this.recommendations.addAll(chatsEntity)
            this.fullRecommendations.addAll(chatsEntity)

            viewState?.showRecommendations(recommendations)
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    private fun convertDiscoverItemsToChatEntityList(chats: List<DiscoverItems.Item>): List<ChatEntity> {
        val newChats = arrayListOf<ChatEntity>()

        for (chat in chats) {
            val chatEntity = ChatEntity(
                0,
                chat.entityID.asUnescapedString(),
                chat.name,
                true,
                0
            )
            newChats.add(chatEntity)
        }
        return newChats
    }

    fun setFilter(filter: String) {
        if(filter.isBlank()){
            recommendations.clear()
            recommendations.addAll(fullRecommendations)
            viewState?.onDataChange()
        }else{
            val list = fullRecommendations.filter {
                it.chatName.contains(filter, true)
            }
            recommendations.clear()
            recommendations.addAll(list)
            viewState?.onDataChange()
        }
    }
}