package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.common.utils.Utils
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.MucUseCase
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*


@InjectViewState
class MapPresenter : MvpPresenter<MapMainView>() {

    private var useCase: EventsUseCase? = null
    private var mucUseCase: MucUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = EventsUseCase()
        mucUseCase = MucUseCase()
    }

    fun getRooms(lat: String, lng: String, radius: String, category: Category?) {
        if (RoomsMap.isFilter) {
            getRoomsByCategory(lat, lng, radius, RoomsMap.category!!)
        } else {
            //this hard data - center Moscow
            compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { rooms, throwable ->
                    if (throwable == null) {
                        RoomsMap.clean()
                        RoomsMap.rooms = rooms
                        Log.d("rooms", rooms.size.toString())
                        viewState?.showRoomsOnMap(rooms)
                    } else {
                        throwable.message?.let { viewState?.showError(it) }
                    }
                })
        }
    }

    fun getRoomsByCategory(lat: String, lng: String, radius: String, category: Category) {
        compositeDisposable.add(useCase!!.getRoomsByCategory(
            category.id,
            "55.751244",
            "37.618423",
            10000.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    RoomsMap.clean()
                    RoomsMap.rooms = rooms
                    Log.d("rooms", rooms.size.toString())
                    viewState?.showRoomsOnMap(rooms)
                } else {
                    throwable.message?.let { viewState?.showError(it) }
                }
            })
    }

    fun getValueOnlineUsers(jid: String): Int {
        val groupId = JidCreate.entityBareFrom(jid)

        val muc =
            MainApplication.getXmppConnection().multiUserChatManager
                .getMultiUserChat(groupId)
        val members = muc.occupants

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

    fun getCardInfo(event: RoomPin) {
        mucUseCase?.getRoomInfo(event.roomID!!)?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                try {
                    viewState?.showEventName(it.name)
                    viewState?.showDescriptionEvent(
                        it.description
                    )
                    val onlineUsers = getValueOnlineUsers(it.room.asUnescapedString())
                    viewState?.showOnlineUserRoomInfo("${it.occupantsCount} человек, $onlineUsers онлайн")
                    setAvatar(it.room.asUnescapedString(), it.name)
                } catch (e: Exception) {
                    Logger.d(e)
                }
            }, {
                viewState?.showEventName(event.name!!)
                setAvatar(event.roomID!!, event.name!!)
                Logger.d(it)
            })
        val distance = (calculationByDistance(
            event.latitude.toString(),
            event.longitude.toString()
        ))
        viewState?.showDistance(distance)
    }

    private fun setAvatar(jid: String, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        viewState?.showAvatar(avatar)
                    }
                }, { throwable -> Logger.d(throwable) })
        }
    }

    private fun calculationByDistance(latRoom: String, lngRoom: String): String {
        MainApplication.getCurrentLocation()?.let {
            val myLat = MainApplication.getCurrentLocation().latitude
            val myLng = MainApplication.getCurrentLocation().longitude

            val km = SphericalUtil.computeDistanceBetween(
                LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                LatLng(myLat, myLng)
            ).toInt() / 1000
            return if (km < 1) {
                (SphericalUtil.computeDistanceBetween(
                    LatLng(
                        latRoom.toDouble(),
                        lngRoom.toDouble()
                    ), LatLng(myLat, myLng)
                ).toInt()).toString() + " метрах"
            } else {
                (SphericalUtil.computeDistanceBetween(
                    LatLng(latRoom.toDouble(), lngRoom.toDouble()),
                    LatLng(myLat, myLng)
                ).toInt() / 1000).toString() + " км"
            }
        }
        return ""
    }

    fun isJoin(jid: String): Boolean {
        return try {
            val manager =
                MainApplication.getXmppConnection().multiUserChatManager
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            muc.isJoined
        } catch (e: Exception) {
            Logger.d(e.message)
            false
        }
    }

    @SuppressLint("CheckResult")
    fun joinChat(jid: String) {
        try {
            val manager =
                MainApplication.getXmppConnection().multiUserChatManager
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            val nameRoom =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(muc.room).name

            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = Resourcepart.from(card.nickName)

            if (!muc.isJoined) {
                muc.join(nickName)

                android.os.Handler().postDelayed({
                    MainApplication.getXmppConnection().addChatStatusListener(jid)
                    MainApplication.getXmppConnection().addUserStatusListener(jid)
                }, 2000)
            }

            val chatEntity = ChatEntity(
                jid = jid,
                chatName = nameRoom,
                isGroupChat = true,
                unreadMessagesCount = 0
            )

            ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (muc.isJoined) {
                        viewState?.showChatScreens(jid, "join")
                    }
                }, {
                    if (it is NotFoundException) {
                        ChatListRepository.addChat(chatEntity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (muc.isJoined) {
                                    viewState?.hideJoinButtonsBottomSheet()
                                    viewState?.showChatScreens(jid, "join")
                                }
                            }, { throwable ->
                                throwable.message?.let { it1 -> viewState?.showError(it1) }
                            })
                    }
                })
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        } ?: viewState?.showError("Ошибка")
    }

    fun readChat(jid: String) {
        try {
            val manager =
                MainApplication.getXmppConnection().multiUserChatManager
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)

            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = Resourcepart.from(card.nickName)

            if (!muc.isJoined) {
                muc.join(nickName)
            }


            viewState?.showChatScreens(jid, "read")
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
            ?: viewState?.showError("Ошибка")
    }

    fun setDateFilter(date:String,calendar: Calendar?=null){
        val today = Calendar.getInstance()
        val filteredRooms = arrayListOf<RoomPin>()

        when (date) {
            "Сегодня" -> {
                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==today.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState?.showRoomsOnMap(filteredRooms)
            }
            "Завтра" -> {
                val tomorrow = Calendar.getInstance()
                tomorrow.add(Calendar.DATE, 1)

                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==tomorrow.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState?.showRoomsOnMap(filteredRooms)
            }
            "В выходные" -> {
                val saturday = Utils.getNextSaturdayDate()
                val sunday = Utils.getNextSundayDate()

                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==saturday.get(Calendar.DAY_OF_MONTH) || time.dayOfMonth==sunday.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState?.showRoomsOnMap(filteredRooms)
            }
            "Выбрать дату" -> {
                for (i in RoomsMap.rooms.indices){
                    val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)
                    if(time.dayOfMonth==calendar!!.get(Calendar.DAY_OF_MONTH)){
                        filteredRooms.add(RoomsMap.rooms[i])
                    }
                }
                RoomsMap.rooms = filteredRooms
                viewState?.showRoomsOnMap(filteredRooms)
            }
        }
        RoomsMap.isFilter = true
        RoomsMap.isFilterDate = true
    }

    override fun onDestroy() {
        super.onDestroy()
        // todo
        //  made clear filter when close map fragment
        RoomsMap.clearFilters()
        compositeDisposable.clear()
    }
}