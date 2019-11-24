package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.presentation.view.MapMainView
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter

class MapPresenter : MvpPresenter<MapMainView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }


    fun getRooms(lat: String, lng: String, radius: String) {
        compositeDisposable.add(useCase!!.getRooms(lat, lng, radius)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError {

            }
            .subscribe { t1, t2 ->
                var response = ""
            })
    }


}