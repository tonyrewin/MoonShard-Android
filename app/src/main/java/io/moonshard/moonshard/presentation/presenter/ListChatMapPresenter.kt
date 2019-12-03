package io.moonshard.moonshard.presentation.presenter

import android.util.Log
import io.moonshard.moonshard.presentation.view.ListChatMapView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class ListChatMapPresenter : MvpPresenter<ListChatMapView>(){

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getChats() {
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
                    val kek = ""
                }
            })
    }

}