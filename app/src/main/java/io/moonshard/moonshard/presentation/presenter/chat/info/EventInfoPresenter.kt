package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.presentation.view.chat.info.EventInfoView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.Occupant
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@InjectViewState
class EventInfoPresenter : MvpPresenter<EventInfoView>() {
    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getRoomInfo(jid: String) {
        var eventId: Long? = null
        for (i in RoomsMap.rooms.indices) {
            if (jid == RoomsMap.rooms[i].roomId) {
                eventId = RoomsMap.rooms[i].id
            }
        }

        viewState?.showProgressBar()
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
                        viewState?.hideProgressBar()
                        Logger.d(throwable)
                    }
                })
        }
    }

    fun getOrganizerInfo(eventJid: String) {
        val allEvents = RoomsMap.rooms

        for (i in allEvents.indices) {
            if (eventJid == allEvents[i].roomId) {
                if (allEvents[i].parentGroupId.isNullOrEmpty()) {
                    viewState?.hideOrganizerLayout()
                } else {
                    val organizerJid = allEvents[i].parentGroupId!!
                    showOrganizerInfo(organizerJid)
                }
                break
            }
        }
    }

    private fun showOrganizerInfo(organizerJid: String) {
        Single.create<HashMap<String, String>> {
            try {
                val muc = MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(organizerJid))

                val organizerName = MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(muc.room).name

                val info = HashMap<String, String>()
                info["organizerJid"] = organizerJid
                info["organizerName"] = organizerName
                it.onSuccess(info)
            } catch (e: Exception) {
                Logger.d(e)
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                val organizerName = result["organizerName"]
                val organizerJid = result["organizerJid"]
                viewState?.showDataOrganizer(organizerName!!)
                getAvatarOrganizer(organizerJid!!, organizerName!!)
            }, { throwable ->
                Logger.d(throwable)
            })
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

            getAvatarEvent(jid, roomInfo.name)

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

            // TODO: move this code to EventInfoView
            val date = DateHolder(ChangeEventRepository.event?.eventStartDate!!)
            //if(date.alreadyComeDate()){ iconStartDate.visibility = View.VISIBLE } else{ iconStartDate.visibility = View.GONE}
            viewState.setStartDate("${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} @ ${date.hour}:${date.minute}")


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
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
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

    private fun getAvatarEvent(jid: String, nameChat: String) {
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

    private fun getAvatarOrganizer(jid: String, nameChat: String) {
        MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ bytes ->
                if (bytes != null) {
                    val avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    viewState?.setAvatarOrganizer(avatar)
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