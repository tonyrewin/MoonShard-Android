package io.moonshard.moonshard.presentation.presenter.chat.info

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.models.jabber.EventManagerUser
import io.moonshard.moonshard.presentation.view.chat.info.ManageEventView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.activities.onboardregistration.VCardCustomManager
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.AuthUseCase
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.Occupant
import org.jivesoftware.smackx.muc.RoomInfo
import org.jxmpp.jid.impl.JidCreate
import retrofit2.HttpException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

/*
members = faсe contorller
 */

@InjectViewState
class ManageEventPresenter : MvpPresenter<ManageEventView>() {

    private var eventsUseCase: EventsUseCase? = null
    private val compositeDisposable = CompositeDisposable()
    private var infoEventMuc: RoomInfo? = null

    private var authUseCase: AuthUseCase? = null

    init {
        eventsUseCase = EventsUseCase()
        authUseCase = AuthUseCase()
    }

    fun getInfoChat(jid: String) {
        try {
            viewState?.showProgressBar()
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(jid))

            infoEventMuc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(muc.room)

            viewState?.showName(ChangeEventRepository.name)
            viewState?.showDescription(ChangeEventRepository.description)
            viewState?.showOccupantsCount(infoEventMuc?.occupantsCount.toString())
            viewState?.showAdminsCount(removeDuplicates(muc.moderators).size.toString())
            viewState?.showTimeDays(ChangeEventRepository.event?.ttl!!)
            viewState?.showAdress(
                LatLng(
                    ChangeEventRepository.event!!.latitude,
                    ChangeEventRepository.event!!.longitude
                )
            )

            val calendar =
                convertIsoToCalendar(ChangeEventRepository.event?.eventStartDate!!)

            viewState?.setStartDate(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH)
            )
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            viewState?.showToast("Произошла ошибка")
        }
    }

    private fun convertIsoToCalendar(newStartDate: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(newStartDate)
        return calendar
    }

    /*
    private fun convertUnixTimeStampToCalendar(newStartDate: Long): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(newStartDate * 1000L)
        sdf.format(date)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

     */


    fun setData(
        name: String,
        description: String,
        idChat: String,
        bytes: ByteArray?,
        mimeType: String?,
        event: RoomPin
    ) {
        try {
            viewState.showProgressBar()
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(idChat))

            ChatListRepository.getChatByJidSingle(JidCreate.from(idChat))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.chatName = name //fix bug
                    setAvatarServer(muc, bytes, mimeType)
                    changeDescription(muc, description)
                    changeChatNameServer(muc, it)
                    changeEventServer(event)
                }, {
                    Logger.d(it)
                })
        } catch (e: Exception) {
            e.message?.let { viewState.showToast(it) }
        }
    }

    //ttl - it days in millisec
    fun changeEventServer(
        event: RoomPin
    ) {
        compositeDisposable.add(eventsUseCase!!.changeRoom(
            event
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event, throwable ->
                if (throwable == null) {
                    //viewState.showChatInfo()
                    viewState?.hideProgressBar()
                } else {
                    Logger.d(throwable)
                    viewState?.showToast("Произошла ошибка")
                    viewState?.hideProgressBar()
                }
            })
    }

    private fun changeChatNameServer(muc: MultiUserChat, chat: ChatEntity) {
        try {
            val form = muc.configurationForm
            val answerForm = form.createAnswerForm()
            answerForm.setAnswer("muc#roomconfig_roomname", chat.chatName)
            muc.sendConfigurationForm(answerForm)
            changeChatNameBaseDate(chat)
        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }

    private fun setAvatarServer(muc: MultiUserChat, bytes: ByteArray?, mimeType: String?) {
        try {
            val vm =
                VCardCustomManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCardMuc(muc.room)
            card.setAvatar(bytes, mimeType)
            vm.saveVCard(card, muc.room)
        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }

    private fun changeChatNameBaseDate(chat: ChatEntity) {
        ChatListRepository.changeChatName(chat)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // viewState.showChatInfo()
            }, {
                Logger.d(it)
            })
    }

    private fun changeDescription(muc: MultiUserChat, description: String) {
        try {
            val form = muc.configurationForm
            val answerForm = form.createAnswerForm()
            answerForm.setAnswer("muc#roomconfig_roomdesc", description)
            muc.sendConfigurationForm(answerForm)
        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        //ChangeEventRepository.clean()
    }

    fun destroyRoom(jid: String) {
        try {
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(jid))
            val myJid = MainApplication.getXmppConnection().jid.asUnescapedString()
            val roomJid = JidCreate.entityBareFrom(jid)

            var eventId: String? = null
            for (i in RoomsMap.rooms.indices) {
                if (jid == RoomsMap.rooms[i].roomID) {
                    eventId = RoomsMap.rooms[i].id
                }
            }


            compositeDisposable.add(
                eventsUseCase!!.deleteRoom(
                    eventId!!
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        muc.destroy(myJid, roomJid)
                        viewState?.showChatsScreen()
                    }, {
                        Logger.d(it)
                        viewState?.showToast("Произошла ошибка на сервере")
                    })
            )
        } catch (e: Exception) {
            Logger.d(e)
            viewState?.showToast("Произошла ошибка на сервере")
        }
    }

    fun getVerificationEmail() {
        val accessToken = MainApplication.getCurrentLoginCredentials().accessToken

        Log.d("myTimeUserProfile", accessToken)
        compositeDisposable.add(authUseCase!!.getUserProfileInfo(accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                Log.d("myTimeUserProfile", "kek")
                if (throwable == null) {
                    if (result.isActivated!!) {
                        getPrivateKey()
                        viewState?.initManageTicket(result.isActivated!!)
                    } else {
                        viewState?.initManageTicket(result.isActivated!!)
                    }
                } else {
                    if(throwable is UnknownHostException){
                        viewState?.showToast("Отсутствует интернет-соединение")
                    }else{
                        val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                        val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                        viewState?.showToast(myError.error.message)
                    }
                }
            })
    }

    fun getPrivateKey() {
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(authUseCase!!.getPrivateKey(
            accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->

                if (throwable == null) {
                    if (result.privateKey.isNotBlank()) {
                        MainApplication.initWalletLibrary(result.privateKey)
                    } else {
                        MainApplication.initWalletLibrary(result.privateKey)
                    }
                } else {
                    val jsonError = (throwable as HttpException).response()!!.errorBody()!!.string()
                    val (error) = Gson().fromJson(jsonError, ErrorResponse::class.java)

                    if (error.message == "cipher text too short") {
                        MainApplication.initWalletLibrary(null)
                    }
                    Logger.d(result)
                }
            })
    }

    private fun removeDuplicates(list: List<Occupant>?): ArrayList<Occupant> {
        val s: MutableSet<Occupant> = TreeSet<Occupant>(Comparator<Occupant?> { o1, o2 ->
            if(o1!!.jid.asUnescapedString().split("/")[0]==o2!!.jid.asUnescapedString().split("/")[0]){
                0
            }else{
                1
            }
        })
        s.addAll(list!!)

        val newArray = arrayListOf<Occupant>()
        newArray.addAll(s)
        return newArray
    }
}