package io.moonshard.moonshard.presentation.presenter.chat

import com.google.android.gms.maps.model.LatLng
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.ChatInfoView
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.ui.fragments.map.RoomsMap.rooms
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.Affiliate
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class ChatInfoPresenter: MvpPresenter<ChatInfoView>() {

    fun getMembers(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)

            val roomInfo = MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                .getRoomInfo(groupId)

            val members = muc.members


            var location: LatLng? =null
            var category = ""
            val onlineMembersValue = getValueOnlineUsers(muc,members)

            for(i in RoomsMap.rooms.indices){
                if(roomInfo.room.asEntityBareJidString() == rooms[i].roomId){
                    location = LatLng(rooms[i].latitude.toDouble(),rooms[i].longtitude.toDouble())
                    category = rooms[i].category.toString()
                }
            }


            viewState?.showData(roomInfo.name,roomInfo.occupantsCount,onlineMembersValue,location,category,roomInfo.description)
            viewState?.showMembers(members)
        } catch (e: Exception) {
            val kek = ""
            //e.message?.let { viewState?.showError(it) }
        }
    }

    private fun getValueOnlineUsers(muc: MultiUserChat, members:List<Affiliate>):Int{
        var onlineValue = 0
        for(i in members.indices){
            val user =  muc.getOccupantPresence(JidCreate.entityFullFrom(members[i].jid))
            if(user.type == Presence.Type.available){
                onlineValue++
            }
        }
        return onlineValue
    }



}