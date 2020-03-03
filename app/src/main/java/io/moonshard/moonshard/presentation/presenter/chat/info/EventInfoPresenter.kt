package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.presentation.view.chat.info.EventInfoView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
import java.text.SimpleDateFormat
import java.util.*


@InjectViewState
class EventInfoPresenter : MvpPresenter<EventInfoView>() {
    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getRoomInfo(jid: String) {
        var eventId: Long?=null
        for (i in RoomsMap.rooms.indices) {
            if (jid == RoomsMap.rooms[i].roomId) {
                eventId = RoomsMap.rooms[i].id
            }
        }

        eventId?.let {
            compositeDisposable.add(useCase!!.getRoom(
                it
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event, throwable ->
                    if (throwable == null) {
                        ChangeEventRepository.event = event
                        getMembers(jid)
                    } else {
                        Logger.d(throwable)
                    }
                })
        }
    }

    private fun getMembers(jid: String) {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)

            val roomInfo =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(groupId)

            ChangeEventRepository.description = roomInfo.description
            ChangeEventRepository.name = roomInfo.name

            val members = muc.occupants
            val occupants = arrayListOf<Occupant>()

            val location: LatLng?
            val category: String =
                ChangeEventRepository.event?.category?.get(0)?.categoryName.toString()
            val onlineMembersValue = getValueOnlineUsers(muc, members)

            val calendar =
                convertUnixTimeStampToCalendar(ChangeEventRepository.event?.eventStartDate!!)




            location = LatLng(
                ChangeEventRepository.event!!.latitude,
                ChangeEventRepository.event!!.longitude
            )

            getAvatar(jid, roomInfo.name)

            //todo fix
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

            viewState?.setStartDate(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH)
            )

            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val myNickName = card.nickName

            for (i in members.indices) {
                if (members[i].asUnescapedString().contains(myNickName)) {
                    members.remove(members[i])
                    break
                }
            }

            for (i in members.indices) {
                occupants.add(muc.getOccupant(members[i]))
            }

            viewState?.showMembers(occupants)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }


    private fun convertUnixTimeStampToCalendar(newStartDate: Long): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(newStartDate * 1000L)
        sdf.format(date)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    private fun getAvatar(jid: String, nameChat: String) {
        MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
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
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            muc.leave()
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
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            val moderators = muc.moderators
            isAdminFromOccupants(moderators)
        } catch (e: Exception) {
            false
        }
    }

    private fun isAdminFromOccupants(admins: List<Occupant>): Boolean {
        val myJid = SecurePreferences.getStringValue("jid", null)
        myJid?.let {
            for (i in admins.indices) {
                val adminJid = admins[0].jid.asUnescapedString().split("/")[0]
                if (adminJid == it) {
                    return true
                }
            }
        }
        return false
    }
}