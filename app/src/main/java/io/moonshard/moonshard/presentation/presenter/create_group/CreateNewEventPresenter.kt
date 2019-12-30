package io.moonshard.moonshard.presentation.presenter.create_group

import android.annotation.SuppressLint
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.CreateNewEventView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.Occupant
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*

@InjectViewState
class CreateNewEventPresenter : MvpPresenter<CreateNewEventView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()
    private val events = arrayListOf<RoomPin>()

    init {
        useCase = RoomsUseCase()
    }

    fun getCategories() {
        viewState?.showProgressBar()
        compositeDisposable.add(useCase!!.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { categories, throwable ->
                viewState?.hideProgressBar()
                if (throwable == null) {
                    viewState?.showCategories(categories)
                } else {
                    viewState?.showToast("Ошибка: ${throwable.message}")
                }
            })
    }

    @SuppressLint("CheckResult")
    fun createGroupChat(
        username: String, latitude: Double?, longitude: Double?,
        ttl: Int,
        category: Category?,
        group: ChatEntity?, eventStartDate: Long
    ) {

        if (latitude != null && longitude != null && category != null) {
            val actualUserName: String
            val jidRoomString = UUID.randomUUID().toString() + "@conference.moonshard.tech"

            if (username.contains("@")) {
                viewState?.showToast("Вы ввели недопустимый символ")
                return
            } else {
                actualUserName = username.split("@")[0]
            }

            try {
                val manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val entityBareJid = JidCreate.entityBareFrom(jidRoomString)
                val muc = manager.getMultiUserChat(entityBareJid)
                val nickName =
                    Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)

                muc.create(nickName)
                // room is now created by locked
                val form = muc.configurationForm
                val answerForm = form.createAnswerForm()
                answerForm.setAnswer("muc#roomconfig_persistentroom", true)
                answerForm.setAnswer("muc#roomconfig_roomname", actualUserName)
                muc.sendConfigurationForm(answerForm)

                val chatEntity = ChatEntity(
                    0,
                    jidRoomString,
                    actualUserName,
                    true,
                    0
                )

                ChatListRepository.addChat(chatEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        createRoomOnServer(
                            latitude,
                            longitude,
                            ttl,
                            jidRoomString,
                            category,
                            group,
                            eventStartDate
                        )
                    }
            } catch (e: Exception) {
                e.message?.let { viewState?.showToast(it) }
            }
        } else {
            viewState?.showToast("Заполните поля")
        }
    }

    fun getGroups() {
        ChatListRepository.getChats()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ chats ->
                getAdminChats(chats)
            }, { throwable ->
                throwable.message?.let { viewState?.showToast(it) }
            })
    }

    private fun getAdminChats(chats: List<ChatEntity>) {
        val adminChats = arrayListOf<ChatEntity>()

        for (i in chats.indices) {
            if (isAdminInChat(chats[i].jid)) {
                adminChats.add(chats[i])
            }
        }

        for (i in adminChats.indices) {
            for(k in events.indices){
                if(adminChats[i].jid==events[k].roomId){
                    adminChats.remove(adminChats[i])
                }
            }
        }
        viewState?.showAdminChats(adminChats)
    }

    private fun isAdminInChat(jid: String): Boolean {
        return try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
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

    fun getRooms() {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    events.addAll(rooms)
                    getGroups()
                } else {
                    // throwable.message?.let { viewState?.showError(it) }
                }
            })
    }

    private fun createRoomOnServer(
        latitude: Double?, longitude: Double?, ttl: Int, roomId: String,
        category: Category, group: ChatEntity?, eventStartDate: Long
    ) {
        val categories = arrayListOf<Category>()
        categories.add(category)
        compositeDisposable.add(useCase!!.putRoom(
            latitude,
            longitude,
            ttl,
            roomId,
            categories,
            group?.jid,
            eventStartDate
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { _, throwable ->
                if (throwable == null) {
                    viewState?.showMapScreen()
                    ChooseChatRepository.clean()
                } else {
                    viewState?.showToast("Ошибка: ${throwable.message}")
                }
            })

    }
}