package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.util.Log
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.common.NotFoundException2
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.ListChatMapView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

@InjectViewState
class ListChatMapPresenter : MvpPresenter<ListChatMapView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getChats() {
        if(RoomsMap.isFilter){
            getRoomsByCategory("","","",RoomsMap.category!!)
        }else{
            //this hard data - center Moscow
            compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { rooms, throwable ->
                    if (throwable == null) {
                        RoomsMap.clean()
                        RoomsMap.rooms = rooms
                        Log.d("rooms", rooms.size.toString())
                        viewState?.setChats(rooms)
                    } else {
                        val error = ""
                    }
                })
        }
    }

    private fun getRoomsByCategory(lat: String, lng: String, radius: String, category: Category){
        compositeDisposable.add(useCase!!.getRoomsByCategory(category.id,"55.751244", "37.618423", 10000.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    RoomsMap.clean()
                    RoomsMap.rooms = rooms
                    Log.d("rooms", rooms.size.toString())
                    viewState?.setChats(rooms)
                } else {
                    val error = ""
                }
            })
    }

    @SuppressLint("CheckResult")
    fun joinChat(jid: String) {
            try {

                val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val card = vm.loadVCard()
                val nickName = Resourcepart.from(card.nickName)


                val manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val entityBareJid = JidCreate.entityBareFrom(jid)
                val muc = manager.getMultiUserChat(entityBareJid)
                val info =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                        .getRoomInfo(muc.room)
                val roomName = info.name

                if(!muc.isJoined){
                    muc.join(nickName)
                }

                val chatEntity = ChatEntity(
                    jid = jid,
                    chatName = roomName,
                    isGroupChat = true,
                    unreadMessagesCount = 0
                )

                ChatListRepository.getChatByJid(JidCreate.from(jid))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (muc.isJoined) {
                            viewState?.showChatScreens(jid)
                        }
                    },{
                        if(it is NotFoundException) {
                            ChatListRepository.addChat(chatEntity)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (muc.isJoined) {
                                        viewState?.showChatScreens(jid)
                                    }
                                }, { throwable ->
                                    Logger.d(throwable.message)
                                })
                        }
                    })

            } catch (e: Exception) {
                Logger.d(e.message)
            }
    }
}