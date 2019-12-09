package io.moonshard.moonshard.presentation.presenter.chat

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.ChatInfoView
import io.moonshard.moonshard.ui.fragments.map.RoomsMap.rooms
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
class ChatInfoPresenter : MvpPresenter<ChatInfoView>() {

    fun getMembers(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)

            val roomInfo =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(groupId)

            val members = muc.occupants

            var location: LatLng? = null
            var category = ""
            val onlineMembersValue = getValueOnlineUsers(muc, members)

            for (i in rooms.indices) {
                if (roomInfo.room.asEntityBareJidString() == rooms[i].roomId) {
                    location = LatLng(rooms[i].latitude.toDouble(), rooms[i].longitude.toDouble())
                    category = rooms[i].category.toString()
                }
            }

            getAvatar(jid)
            viewState?.showData(
                roomInfo.name,
                roomInfo.occupantsCount,
                onlineMembersValue,
                location,
                category,
                roomInfo.description
            )
            viewState?.showMembers(members)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
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


    //muc.getOccupantPresence(JidCreate.entityFullFrom("dgggrrg@conference.moonshard.tech/just")) must be
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

    fun leaveGroup(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            muc.leave()
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }
}