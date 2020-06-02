package io.moonshard.moonshard.presentation.presenter.chat.info

import android.util.Log
import com.example.moonshardwallet.MainService
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.info.ManageEventView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.activities.onboardregistration.VCardCustomManager
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.AuthUseCase
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.RoomInfo
import org.jxmpp.jid.impl.JidCreate
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

@InjectViewState
class ManageEventPresenter : MvpPresenter<ManageEventView>() {

    private var roomsUseCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()
    private var infoEventMuc: RoomInfo? = null

    private var authUseCase: AuthUseCase? = null


    init {
        roomsUseCase = RoomsUseCase()
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
            viewState?.showAdminsCount(muc.moderators.size.toString())
            viewState?.showTimeDays(ChangeEventRepository.event?.ttl!!)
            viewState?.showAdress(
                LatLng(
                    ChangeEventRepository.event!!.latitude,
                    ChangeEventRepository.event!!.longitude
                )
            )

            val calendar =
                convertUnixTimeStampToCalendar(ChangeEventRepository.event?.eventStartDate!!)

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

    private fun convertUnixTimeStampToCalendar(newStartDate: Long): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(newStartDate * 1000L)
        sdf.format(date)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

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
                    it.chatName = name
                    setAvatarServer(muc, bytes, mimeType)
                    changeDescription(muc, description)
                    changeChatNameServer(muc, it)
                    changeEventServer(event)
                }, {

                })
        } catch (e: Exception) {
            e.message?.let { viewState.showToast(it) }
        }
    }

    //ttl - it days in millisec
    fun changeEventServer(
        event: RoomPin
    ) {
        compositeDisposable.add(roomsUseCase!!.changeRoom(
            event
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event, throwable ->
                if (throwable == null) {
                    viewState.showChatInfo()
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

            var eventId: Long? = null
            for (i in RoomsMap.rooms.indices) {
                if (jid == RoomsMap.rooms[i].roomId) {
                    eventId = RoomsMap.rooms[i].id
                }
            }


            compositeDisposable.add(roomsUseCase!!.deleteRoom(
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

        Log.d("myTimeUserProfile",accessToken)
        compositeDisposable.add(authUseCase!!.getUserProfileInfo(accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                Log.d("myTimeUserProfile","kek")
                if (throwable == null) {
                 if(result.isActivated!!){
                     MainApplication.initWalletLibrary()
                     viewState?.initManageTicket(result.isActivated!!)
                 }else{
                     viewState?.initManageTicket(result.isActivated!!)
                 }
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showToast(myError.error.message)
                }
            })
    }
}