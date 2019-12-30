package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.presentation.view.CategoriesMapView
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class CategoriesMapPresenter: MvpPresenter<CategoriesMapView>() {
    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getCategories(){
        compositeDisposable.add(useCase!!.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { categories, throwable ->
                if (throwable == null) {
                    viewState?.showCategories(categories)
                }
            })
    }
}