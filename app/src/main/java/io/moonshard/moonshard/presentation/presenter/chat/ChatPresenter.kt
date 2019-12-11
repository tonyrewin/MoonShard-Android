package io.moonshard.moonshard.presentation.presenter.chat

import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.ChatView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>(){

    private lateinit var chatID: String

    fun setChatId(chatId: String) {
        chatID = chatId
        getDataInfo()
    }

    private fun getDataInfo(){
        try {
            val groupId = JidCreate.entityBareFrom(chatID)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val roomInfo =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(groupId)
            val occupants = muc.occupants

            val name = roomInfo.name
            getAvatar(chatID)
            val valueOccupants = roomInfo.occupantsCount
            val valueOnlineMembers = getValueOnlineUsers(muc,occupants)
            viewState?.setData(name,valueOccupants,valueOnlineMembers)
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }

    private fun getValueOnlineUsers(muc: MultiUserChat, members: List<EntityFullJid>): Int {
        var onlineValue = 0
        for (i in members.indices) {
            val userOccupantPresence = muc.getOccupantPresence(members[i].asEntityFullJidIfPossible())
            if (userOccupantPresence.type == Presence.Type.available) {
                onlineValue++
            }
        }
        return onlineValue
    }

    private fun getAvatar(jid: String) {
        MainApplication.getXmppConnection().loadAvatar(jid)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ bytes ->
                if (bytes != null) {
                    val avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    viewState?.setAvatar(avatar)
                }
            }, { throwable ->
                Log.e(throwable.message)
            })
    }
}