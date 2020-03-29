package io.moonshard.moonshard.presentation.presenter.create_group

import android.annotation.SuppressLint
import android.util.Log
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.CreateNewEventView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.activities.onboardregistration.VCardCustomManager
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.Occupant
import org.jivesoftware.smackx.vcardtemp.packet.VCard
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        nameEvent: String, latitude: Double?, longitude: Double?,
        ttl: Int,
        category: Category?,
        group: ChatEntity?, eventStartDate: Long,address:String
    ) {

        if (latitude != null && longitude != null && category != null) {
            val actualUserName: String
            val jidRoomString = UUID.randomUUID().toString()+"-event" + "@conference.moonshard.tech"

            if (nameEvent.contains("@")) {
                viewState?.showToast("Вы ввели недопустимый символ")
                return
            } else {
                actualUserName = nameEvent.split("@")[0]
            }

            try {
                val manager =
                    MainApplication.getXmppConnection().multiUserChatManager
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
                answerForm.setAnswer("muc#roomconfig_publicroom",true)
                val arrayList = arrayListOf<String>()
                arrayList.add("anyone")
                answerForm.setAnswer("muc#roomconfig_whois",arrayList)
                muc.sendConfigurationForm(answerForm)

                val vm = VCardCustomManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val vcard = VCard()
                vm.saveVCard(vcard, JidCreate.entityBareFrom(jidRoomString))

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
                    .subscribe ({
                        createRoomOnServer(
                            latitude,
                            longitude,
                            ttl,
                            jidRoomString,
                            category,
                            group,
                            eventStartDate,nameEvent,address
                        )
                    },{
                        Logger.d(it)
                    })
            } catch (e: Exception) {
                e.message?.let { viewState?.showToast(it) }
            }
        } else {
            viewState?.showToast("Заполните поля")
        }


    }

    private fun getGroups() {
        ChatListRepository.getChats()
            .subscribeOn(Schedulers.io())
            .map {
                Log.d("TAAAAg", Thread.currentThread().name)
                it.filter { chatEntity -> chatEntity.jid.contains("chat") }
            }
            .observeOn(AndroidSchedulers.mainThread())
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

        val newAdminChats = arrayListOf<ChatEntity>()
        newAdminChats.addAll(adminChats)

        for (i in adminChats.indices) {
            for(k in events.indices){
                if(adminChats[i].jid==events[k].roomId){
                    newAdminChats.remove(adminChats[i])
                }
            }
        }
        viewState?.showAdminChats(adminChats)
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

    fun getRooms() {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
        category: Category, group: ChatEntity?, eventStartDate: Long,name:String,address:String
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
            eventStartDate,name,address
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _, throwable ->
                if (throwable == null) {
                    viewState?.showMapScreen()
                    ChooseChatRepository.clean()

                    //important
                    MainApplication.getXmppConnection().addUserStatusListener(roomId)
                    MainApplication.getXmppConnection().addChatStatusListener(roomId)
                    MainApplication.getXmppConnection().joinChat(roomId)
                } else {
                    viewState?.showToast("Ошибка: ${throwable.message}")
                }
            })
    }
}