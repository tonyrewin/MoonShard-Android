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
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URLConnection
import java.util.*
import android.graphics.Bitmap
import io.moonshard.moonshard.common.utils.Utils.stringToBitMap
import org.jivesoftware.smackx.muc.*


@InjectViewState
class ChatInfoPresenter : MvpPresenter<ChatInfoView>() {

    fun getMembers(jid: String) {
        try {
            viewState?.showProgressBar()

            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)

            val roomInfo =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(groupId)

            val members = muc.occupants
            var occupants = arrayListOf<Occupant>()

            var location: LatLng? = null
            var category = ""
            val onlineMembersValue = getValueOnlineUsers(muc, members)

            for (i in rooms.indices) {
                if (roomInfo.room.asEntityBareJidString() == rooms[i].roomID) {
                    location = LatLng(rooms[i].latitude.toDouble(), rooms[i].longitude.toDouble())
                    category = rooms[i].category?.get(0)?.categoryName.toString()
                }
            }

            getAvatar(jid,roomInfo.name)

            //todo fix
            val isAdmin = isManager(jid)

            if (isAdmin) {
                val type = getTypeAdmin(jid)
                if (type != null) viewState?.showChangeChatButton(
                    true,
                    type
                ) else viewState?.showChangeChatButton(
                    false, null
                )
            } else {
                viewState?.showChangeChatButton(
                    false, null
                )
            }

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
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            e.message?.let { viewState?.showError(it) }
        }
    }

    private fun getAvatar(jid: String,nameChat:String) {
            MainApplication.getXmppConnection().loadAvatar(jid,nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
            removeChatFromBd(jid)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }

    private fun removeChatFromBd(jid: String) {
        ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chat ->
                ChatListRepository.removeChat(chat)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val groupId = JidCreate.entityBareFrom(jid)
                        val muc =
                            MainApplication.getXmppConnection().multiUserChatManager
                                .getMultiUserChat(groupId)
                        muc.leave()
                        viewState?.showChatsScreen()
                    }, { throwable ->
                        throwable.message?.let { it1 -> viewState?.showError(it1) }
                    })
            }, { error ->
                Logger.d(error)
            })
    }

    fun getTypeAdmin(eventJid: String): String? {
        try {
            val myJid = SecurePreferences.getStringValue("jid", null)

            val groupId = JidCreate.entityBareFrom(eventJid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators

            myJid?.let {
                for (i in moderators.indices) {
                    val adminJid = moderators[i].jid.asUnescapedString().split("/")[0]
                    if (adminJid == it) {
                        when (moderators[i].affiliation) {
                            MUCAffiliation.owner -> {
                                return "owner"
                            }
                            MUCAffiliation.admin -> {
                                return "admin"
                            }
                        }
                    }
                }


            }

        } catch (e: Exception) {
            val myJid = SecurePreferences.getStringValue("jid", null)

            val groupId = JidCreate.entityBareFrom(eventJid)

            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val faceControllers = muc.members

            myJid?.let {
                for (i in faceControllers.indices) {
                    if (faceControllers[i].jid.asUnescapedString() == it) {
                        return "FaceController"
                    }
                }
            }
            return null
        }
        return null
    }

    private fun isManager(jid: String): Boolean {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators

            val isAdminOrOwner = isAdminOrOwnerFromOccupants(moderators)
            return if (isAdminOrOwner) {
                true
            } else {
                val faceControllers = muc.members
                val arrayListFaceControllers = arrayListOf<Affiliate>()
                arrayListFaceControllers.clear()
                arrayListFaceControllers.addAll(faceControllers)
                isFaceController(arrayListFaceControllers)
            }
        } catch (e: Exception) {
            return try {
                val groupId = JidCreate.entityBareFrom(jid)
                val muc =
                    MainApplication.getXmppConnection().multiUserChatManager
                        .getMultiUserChat(groupId)
                val faceControllers = muc.members
                val arrayListFaceControllers = arrayListOf<Affiliate>()
                arrayListFaceControllers.clear()
                arrayListFaceControllers.addAll(faceControllers)
                isFaceController(arrayListFaceControllers)
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun isFaceController(faceControllers: ArrayList<Affiliate>): Boolean {
        val myJid = SecurePreferences.getStringValue("jid", null)
        myJid?.let {
            for (i in faceControllers.indices) {
                val adminJid = faceControllers[i].jid.asUnescapedString()
                if (adminJid == it) {
                    return true
                }
            }
        }
        return false
    }

    //todo fix (how set privileges for all type user?)
    private fun isAdminInChat(jid: String): Boolean {
        return try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators
            isAdminOrOwnerFromOccupants(moderators)
        } catch (e: Exception) {
            false
        }
    }

    private fun isAdminOrOwnerFromOccupants(admins: List<Occupant>): Boolean {
        val myJid = SecurePreferences.getStringValue("jid", null)
        myJid?.let {
            for (i in admins.indices) {
                val adminJid = admins[i].jid.asUnescapedString().split("/")[0]
                if (adminJid == it) {
                    return true
                }
            }
        }
        return false
    }
}