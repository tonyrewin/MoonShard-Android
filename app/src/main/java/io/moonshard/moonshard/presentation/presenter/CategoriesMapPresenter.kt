package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.models.api.Category
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
        val categories = arrayListOf<Category>()
        categories.add(Category(1,"Тусовки"))
        categories.add(Category(2,"Бизнес ивенты"))
        categories.add(Category(3,"Кружок по интересам"))
        categories.add(Category(4,"Культурные мероприятия"))
        viewState?.showCategories(categories)
    }
}