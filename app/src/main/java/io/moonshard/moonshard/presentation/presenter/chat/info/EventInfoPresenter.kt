package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
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

        viewState?.showProgressBar()

        ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chatEntity ->
                var eventId: String? = getEventId(jid)

                if (eventId == null) {
                    if (chatEntity.event != null) {
                        ChangeEventRepository.event = Gson().fromJson(chatEntity.event,RoomPin::class.java)
                        getMembers(jid)
                        showOrganizerInfo(ChangeEventRepository.event!!)
                        getStartDateEvent(ChangeEventRepository.event?.eventStartDate!!)
                    }
                } else {
                    getEvent(jid, eventId,chatEntity)
                }
            }, {
                viewState?.hideProgressBar()
                viewState?.showError("Произошла ошибка")
                Logger.d(it)
            })
    }

    private fun getEvent(
        jid: String,
        eventId: String,
        chatEntity: ChatEntity
    ) {
        compositeDisposable.add(useCase!!.getRoom(
            eventId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event, throwable ->
                if (throwable == null) {
                    addEventDataInBd(event,chatEntity)
                    ChangeEventRepository.event = event
                    getMembers(jid)
                    showOrganizerInfo(event)
                    getStartDateEvent(ChangeEventRepository.event?.eventStartDate!!)
                } else {
                    ChangeEventRepository.event = Gson().fromJson(chatEntity.event,RoomPin::class.java)
                    getMembers(jid)
                    getStartDateEvent(ChangeEventRepository.event?.eventStartDate!!)
                    showOrganizerInfo(ChangeEventRepository.event!!)
                    getData()
                    viewState?.hideProgressBar()
                    Logger.d(throwable)
                }
            })
    }

    fun addEventDataInBd(
        event: RoomPin,
        chatEntity: ChatEntity
    ) {
        try {
            val groupId = JidCreate.entityBareFrom(event.roomID)
            val roomInfo =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(groupId)
            event.description = roomInfo.description

            chatEntity.event = Gson().toJson(event)

            ChatListRepository.addChat(chatEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.d("success")
                }, { throwable ->
                    throwable.message?.let { viewState?.showError(it) }
                })
        }catch (e:Exception){
            Logger.d(e)
        }
    }

    private fun getEventId(jid: String): String? {
        for (i in RoomsMap.rooms.indices) {
            if (jid == RoomsMap.rooms[i].roomID) {
                return RoomsMap.rooms[i].id
            }
        }
        return null
    }


    fun showOrganizerInfo(event: RoomPin) {
        if (event.parentGroupId.isNullOrEmpty()) {
            viewState?.hideOrganizerLayout()
        } else {
            val organizerJid = event.parentGroupId!!
            showOrganizerInfo(organizerJid)
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

            val category: String = ChangeEventRepository.event?.category?.get(0)?.categoryName.toString()
            val onlineMembersValue = getValueOnlineUsers(muc, members)


            getAvatarEvent(jid, roomInfo.name)

            val location = LatLng(
                ChangeEventRepository.event!!.latitude,
                ChangeEventRepository.event!!.longitude
            )

            viewState?.showData(
                roomInfo.name,
                roomInfo.occupantsCount,
                onlineMembersValue,location,
                category,
                roomInfo.description,ChangeEventRepository.event!!.address)


            getAvatarEvent(jid, roomInfo.name)


            //todo fix
            val isAdmin = isAdminInChat(jid)
            if (isAdmin) viewState?.showChangeChatButton(true) else viewState?.showChangeChatButton(
                false
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
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            e.message?.let { viewState?.showError(it) }
        }
    }

    private fun getData(){

      val location = LatLng(
            ChangeEventRepository.event!!.latitude,
            ChangeEventRepository.event!!.longitude
        )

        getAvatarEvent(ChangeEventRepository.event!!.roomID!!, ChangeEventRepository.event!!.name!!)

        viewState?.showData(
            ChangeEventRepository.event!!.name!!,
            0,
            0, location,
            ChangeEventRepository.event!!.category!![0].categoryName!!,
            ChangeEventRepository.event!!.description,
            ChangeEventRepository.event!!.address)
    }

    private fun  getStartDateEvent(eventStartDate:String){
        val date = DateHolder(eventStartDate)
        //if(date.alreadyComeDate()){ iconStartDate.visibility = View.VISIBLE } else{ iconStartDate.visibility = View.GONE}
        viewState.setStartDate("${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}")
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