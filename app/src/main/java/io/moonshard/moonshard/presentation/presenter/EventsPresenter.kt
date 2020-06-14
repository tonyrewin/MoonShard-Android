package io.moonshard.moonshard.presentation.presenter

import android.annotation.SuppressLint
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.EventsView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

@InjectViewState
class EventsPresenter : MvpPresenter<EventsView>() {
    private var useCase: EventsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = EventsUseCase()
    }

    fun getRooms() {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    getEvents(ChatRepository.idChatCurrent, rooms)
                } else {
                    throwable.message?.let { viewState?.showError(it) }
                }
            })
    }

    private fun getEvents(jidChat: String?, events: ArrayList<RoomPin>) {
        jidChat?.let {
            val myEvents = arrayListOf<RoomPin>()
            for (i in events.indices) {
                if (jidChat == events[i].parentGroupId) {
                    myEvents.add(events[i])
                }
            }

            if (myEvents.isEmpty()) {
                viewState?.isShowCreateEventLayout(isShow = true, isAdmin = isAdminInChat(jidChat))
            } else {
                viewState?.isShowCreateEventLayout(false, isAdminInChat(jidChat))
                viewState?.setEvents(myEvents)
            }
        }
    }

    @SuppressLint("CheckResult")
    fun joinChat(jid: String) {
        try {
            val manager =
                MainApplication.getXmppConnection().multiUserChatManager
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)
            val info =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(muc.room)
            val roomName = info.name

            if (!muc.isJoined) {
                muc.join(nickName)
            }

            val chatEntity = ChatEntity(
                jid = jid,
                chatName = roomName,
                isGroupChat = true,
                unreadMessagesCount = 0
            )

            ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (muc.isJoined) {
                        viewState?.showChatScreen(jid)
                    }
                }, {
                    if (it is NotFoundException) {
                        ChatListRepository.addChat(chatEntity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (muc.isJoined) {
                                    viewState?.showChatScreen(jid)
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

    private fun isAdminInChat(jid: String): Boolean {
        return try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(groupId)
            isAdminFromOccupants(muc.moderators)
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