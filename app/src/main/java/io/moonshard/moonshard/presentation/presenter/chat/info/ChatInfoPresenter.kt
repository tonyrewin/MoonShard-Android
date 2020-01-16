package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.info.ChatInfoView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap.rooms
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.Occupant
import org.jivesoftware.smackx.vcardtemp.VCardManager
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
            var occupants = arrayListOf<Occupant>()

            var location: LatLng? = null
            var category = ""
            val onlineMembersValue = getValueOnlineUsers(muc, members)

            for (i in rooms.indices) {
                if (roomInfo.room.asEntityBareJidString() == rooms[i].roomId) {
                    location = LatLng(rooms[i].latitude.toDouble(), rooms[i].longitude.toDouble())
                    category = rooms[i].category?.get(0)?.categoryName.toString()
                }
            }

            getAvatar(jid)

            //todo fi
            val isAdmin = isAdminInChat(jid)
            if (isAdmin) viewState?.showChangeChatButton(true) else viewState?.showChangeChatButton(
                false
            )

            viewState?.showData(
                roomInfo.name,
                roomInfo.occupantsCount,
                onlineMembersValue,
                location,
                category,
                roomInfo.description
            )

            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val myNickName = card.nickName

            /*
            //remove user with my nickName
            for (i in members.indices) {
                if (members[i].asUnescapedString().contains(myNickName)) {
                    members.remove(members[i])
                    break
                }
            }
             */

            //
            for (i in members.indices) {
                occupants.add(muc.getOccupant(members[i]))
            }

            val myJid = SecurePreferences.getStringValue("jid", null)

            val iterator = occupants.iterator()
            while (iterator.hasNext()) {
                val occupant = iterator.next()
                if (occupant.jid==null) {
                    iterator.remove()
                }else if(occupant.jid.asBareJid().asUnescapedString()==myJid){
                    iterator.remove()
                }
            }

            viewState?.showMembers(occupants)
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
            val userOccupantPresence =
                muc.getOccupantPresence(members[i].asEntityFullJidIfPossible())
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
            removeChatFromBd(jid)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }

    private fun removeChatFromBd(jid: String) {
        ChatListRepository.getChatByJid(JidCreate.from(jid))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chat ->
                ChatListRepository.removeChat(chat)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewState?.showChatsScreen()
                    }, { throwable ->
                        throwable.message?.let { it1 -> viewState?.showError(it1) }
                    })
            }, { error ->
                Logger.d(error)
            })
    }

    //todo fix (how set privileges for all type user?)
    private fun isAdminInChat(jid: String): Boolean {
        return try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators
            isAdminFromOccupants(muc,moderators)
        } catch (e: Exception) {
            false
        }
    }

    private fun isAdminFromOccupants(muc: MultiUserChat,admins: List<Occupant>): Boolean {
        try {
            val myJid = SecurePreferences.getStringValue("jid", null)
            myJid?.let {
                for (i in admins.indices) {
                    val adminJid =admins[i].jid.asBareJid().asUnescapedString()
                    if (adminJid.contains(it,true)) {
                        return true
                    }
                }
            }
        }catch (e:Exception){
            return false
        }
        return false
    }
}