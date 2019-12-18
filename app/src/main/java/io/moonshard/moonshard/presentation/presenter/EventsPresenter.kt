package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.view.chat.EventsView
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class EventsPresenter : MvpPresenter<EventsView>() {
    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }


    fun getRooms() {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    getEvents(ChatRepository.idChatCurrent!!,rooms)
                } else {
                    throwable.message?.let { viewState?.showError(it) }
                }
            })
    }

    private fun getEvents(jidChat: String, events: ArrayList<RoomPin>) {
        val myEvents = arrayListOf<RoomPin>()
        for (i in events.indices) {
            if (jidChat == events[i].groupId) {
                myEvents.add(events[i])
            }
        }
        viewState?.setEvents(myEvents)
    }
}