package io.moonshard.moonshard.presentation.presenter.chat

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.RecommendationsView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.disco.packet.DiscoverItems
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class RecommendationsPresenter : MvpPresenter<RecommendationsView>() {


    fun getRecommendation() {
        try {
            val chatsAndEvents =
                MainApplication.getXmppConnection().getServiceDiscoveryManager()!!.discoverItems(
                    JidCreate.from("conference.moonshard.tech")
                )
                    .items

            val chats = chatsAndEvents.filter { it.entityID.contains("chat") }

            val chatsEntity = convertDiscoverItemsToChatEntityList(chats)

            viewState?.showRecommendations(chatsEntity)
        } catch (e: Exception) {

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
}