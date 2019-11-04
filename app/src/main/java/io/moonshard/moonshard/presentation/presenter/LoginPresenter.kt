package io.moonshard.moonshard.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.usecase.TestUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

    @Inject
    internal lateinit var api: API

    private var testUseCase: TestUseCase? = null

    private val compositeDisposable = CompositeDisposable()

    init {
        MainApplication.getComponent().inject(this)
        testUseCase = TestUseCase()
    }

    fun login(homeserverUri: String, identityUri: String, email: String, password: String) {
        compositeDisposable.add(testUseCase!!.getTest().
        observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError {
                var kek = ""
                viewState?.hideLoader()
            }
            .subscribe { t1, t2 ->
                var kek = ""
                viewState?.hideLoader()
            })
        //viewState?.showLoader()
    }
}