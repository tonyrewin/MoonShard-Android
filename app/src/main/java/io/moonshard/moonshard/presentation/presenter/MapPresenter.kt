package io.moonshard.moonshard.presentation.presenter

import android.util.Log
import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.ui.fragments.map.RoomsMap
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class MapPresenter : MvpPresenter<MapMainView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getRooms(lat: String, lng: String, radius: String) {
        //this hard data - center Moscow
        compositeDisposable.add(useCase!!.getRooms("55.751244", "37.618423", 10000.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { rooms, throwable ->
                if (throwable == null) {
                    RoomsMap.clean()
                    RoomsMap.rooms = rooms
                    Log.d("rooms", rooms.size.toString())
                    viewState?.showRoomsOnMap(rooms)
                } else {
                    throwable.message?.let { viewState?.showError(it) }
                }
            })
    }
}