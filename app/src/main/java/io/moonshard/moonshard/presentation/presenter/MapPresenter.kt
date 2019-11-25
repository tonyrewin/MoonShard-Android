package io.moonshard.moonshard.presentation.presenter

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
            .doOnError {
                var response = ""
            }
            .doOnSuccess {
                //  var kek = it.get(0).roomId
                //  var response = ""

                //  RoomsMap.clean()
                //  RoomsMap.rooms = it

                //  viewState?.showRoomsOnMap(it)
            }.subscribe { t1, t2 ->
                if (t2 == null) {
                    RoomsMap.clean()
                    RoomsMap.rooms = t1
                    viewState?.showRoomsOnMap(t1)
                }
            })
    }
}