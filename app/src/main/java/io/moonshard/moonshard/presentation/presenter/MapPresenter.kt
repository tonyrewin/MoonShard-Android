package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.util.Log
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
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


@InjectViewState
class MapPresenter : MvpPresenter<MapMainView>() {
    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getRooms(lat: String, lng: String, radius: String, category: Category?) {
        if (RoomsMap.isFilter) {
            getRoomsByCategory(lat, lng, radius, RoomsMap.category!!)
        } else {
            //this hard data - center Moscow
            compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
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
            MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
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

    fun getRoom(jid: String): RoomInfo? {
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val info =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(muc.room)
            return info
        } catch (e: java.lang.Exception) {
            val kek = ""
        }
        return null
    }

    fun isJoin(jid:String):Boolean{
        return try {
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            muc.isJoined
        }catch (e:Exception){
            Logger.d(e.message)
            false
        }
    }

    @SuppressLint("CheckResult")
    fun joinChat(jid: String, nameRoom: String?) {
        nameRoom?.let {
            try {
                val manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val entityBareJid = JidCreate.entityBareFrom(jid)
                val muc = manager.getMultiUserChat(entityBareJid)

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

                ChatListRepository.getChatByJid(JidCreate.from(jid))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (muc.isJoined) {
                            viewState?.showChatScreens(jid,"join")
                        }
                    }, {
                        if (it is NotFoundException) {
                            ChatListRepository.addChat(chatEntity)
                                .observeOn(Schedulers.io())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (muc.isJoined) {
                                        viewState?.showChatScreens(jid,"join")
                                    }
                                }, { throwable ->
                                    throwable.message?.let { it1 -> viewState?.showError(it1) }
                                })
                        }
                    })
            } catch (e: Exception) {
                e.message?.let { viewState?.showError(it) }
            }
        } ?: viewState?.showError("Ошибка")
    }

    fun readChat(jid: String, nameRoom: String?) {
        nameRoom?.let {
            try {
                val manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val entityBareJid = JidCreate.entityBareFrom(jid)
                val muc = manager.getMultiUserChat(entityBareJid)

                val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val card = vm.loadVCard()
                val nickName = Resourcepart.from(card.nickName)

                if (!muc.isJoined) {
                    muc.join(nickName)
                }

                viewState?.showChatScreens(jid,"read")
            } catch (e: Exception) {
                e.message?.let { viewState?.showError(it) }
            }
        } ?: viewState?.showError("Ошибка")
    }
}