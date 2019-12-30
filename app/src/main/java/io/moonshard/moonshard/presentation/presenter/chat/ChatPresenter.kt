package io.moonshard.moonshard.presentation.presenter.chat

import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.view.chat.ChatView
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.EntityFullJid
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log


@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>() {

    private var useCase: RoomsUseCase? = null
    private lateinit var chatID: String
    private lateinit var chatName: String

    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun setChatId(chatId: String) {
        chatID = chatId
        if(chatId.contains("conference")){
            getDataInfoMuc()
        }else{
            getDataInfoUser()
        }
    }

    private fun getDataInfoUser() {
        try {
            val jidUser = JidCreate.entityBareFrom(chatID)
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(jidUser)
            val nickName: String
            nickName = if (card.nickName.isNullOrBlank()) {
                card.to.asBareJid().localpartOrNull.toString()
            } else {
                card.nickName
            }
            getAvatar(chatID,nickName)
            viewState?.setNameUser(nickName)
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }


        fun getDataInfoMuc() {
        try {
            val groupId = JidCreate.entityBareFrom(chatID)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)
            val roomInfo =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getRoomInfo(groupId)
            val occupants = muc.occupants

            val name = roomInfo.name
            getAvatar(chatID,name)
            val valueOccupants = roomInfo.occupantsCount
            val valueOnlineMembers = getValueOnlineUsers(muc, occupants)
            viewState?.setDataMuc(name, valueOccupants, valueOnlineMembers)
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }

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

    private fun getAvatar(jid: String,name:String) {
        MainApplication.getXmppConnection().loadAvatar(jid,name)
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

    fun isEvent() {
        if(chatID.contains("conference")){
            getRooms()
        }else{
            viewState?.initViewPagerFromEvent()
        }
    }

    fun getRooms() {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { rooms, throwable ->
                initPager(rooms)
                if (throwable != null) {
                    throwable.message?.let { viewState?.showError(it) }
                }
            })
    }

    fun initPager(rooms: ArrayList<RoomPin>?) {
        if (rooms != null) {
            for (i in rooms.indices) {
                if (rooms[i].roomId == chatID) {
                    viewState.initViewPagerFromEvent()
                    return
                }
            }
            viewState.initViewPager()
        } else {
            viewState.initViewPager()
        }
    }
}