package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.MucUseCase
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.RoomInfo
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*


@InjectViewState
class MapPresenter : MvpPresenter<MapMainView>() {

    private var useCase: RoomsUseCase? = null
    private var mucUseCase: MucUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
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
        mucUseCase?.getRoomInfo(event.roomId!!)?.subscribeOn(Schedulers.io())
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
                setAvatar(event.roomId!!, event.name!!)
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

    fun setDateFilter(date:String){
        val today = Calendar.getInstance()
        for (i in RoomsMap.rooms.indices){
            val time  = DateHolder(RoomsMap.rooms[i].eventStartDate!!)

           // if(today.get())
        }
    }
}