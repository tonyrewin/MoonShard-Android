package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import android.util.Log
import io.moonshard.moonshard.MainApplication
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
import org.jivesoftware.smackx.muc.MultiUserChatManager
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
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
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)

            if(!muc.isJoined){
                muc.join(nickName)
            }

            val chatEntity = ChatEntity(
                jid = jid,
                chatName = jid.split("@")[0],
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
                    ChatListRepository.addChat(chatEntity)
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe( {
                            if (muc.isJoined) {
                                viewState?.showChatScreens(jid)
                            }
                        },{
                            com.orhanobut.logger.Logger.d(it.message)
                        })
                })
        } catch (e: Exception) {
            com.orhanobut.logger.Logger.d(e.message)
            // e.message?.let { viewState?.showError(it) }
        }
    }

}