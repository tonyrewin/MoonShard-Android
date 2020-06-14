package io.moonshard.moonshard.presentation.presenter.profile.mytickets

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.utils.DateHolder
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.profile.my_tickets.MyTicketInfoView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.EventsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class MyTicketInfoPresenter : MvpPresenter<MyTicketInfoView>() {

    private var useCase: EventsUseCase? = null

    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = EventsUseCase()
    }

    fun getEventInfo(jid: String) {
        ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ chatEntity ->
                var eventId: String? = getEventId(jid)

                if (eventId == null) {
                    if (chatEntity.event != null) {

                        val event = Gson().fromJson(chatEntity.event, RoomPin::class.java)
                        val date = DateHolder(event?.eventStartDate!!)
                        val startDateEvent =
                            "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"
                        viewState?.showEventInfo(event.name!!, startDateEvent, event.address!!)
                        setAvatar(jid, event.name!!)
                    }
                } else {
                    getEvent(jid, eventId, chatEntity)
                }
            }, {
                viewState?.hideProgressBar()
                viewState?.showToast("Произошла ошибка")
                Logger.d(it)
            })
    }

    private fun getEventId(jid: String): String? {
        for (i in RoomsMap.rooms.indices) {
            if (jid == RoomsMap.rooms[i].roomID) {
                return RoomsMap.rooms[i].id
            }
        }
        return null
    }

    private fun getEvent(
        jid: String,
        eventId: String,
        chatEntity: ChatEntity
    ) {
        compositeDisposable.add(useCase!!.getRoom(
            eventId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event, throwable ->
                if (throwable == null) {
                    //work with event
                    val date = DateHolder(event?.eventStartDate!!)
                    val startDateEvent =
                        "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"
                    viewState?.showEventInfo(event.name!!, startDateEvent, event.address!!)
                    setAvatar(jid, event.name!!)

                } else {
                    val eventFromBd = Gson().fromJson(chatEntity.event, RoomPin::class.java)
                    val date = DateHolder(eventFromBd?.eventStartDate!!)
                    val startDateEvent =
                        "${date.dayOfMonth} ${date.getMonthString(date.month)} ${date.year} г. в ${date.hour}:${date.minute}"
                    viewState?.showEventInfo(eventFromBd.name!!, startDateEvent, eventFromBd.address!!)
                    setAvatar(jid, eventFromBd.name!!)
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun setAvatar(jid: String, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatarForTicket(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        MainApplication.getMainUIThread().post {
                            viewState.setAvatar(avatar)
                        }
                    }
                }, { throwable ->
                    throwable.message?.let { Logger.e(it) }
                })
        }
    }


}