package io.moonshard.moonshard.presentation.presenter


import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.usecase.TestUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject


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

    fun login() {
        //this.startService(Intent(this, XMPPConnectionService::class.java))

    }

    fun login(homeserverUri: String, identityUri: String, email: String, password: String) {
        compositeDisposable.add(testUseCase!!.getTest().observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError {
                var kek = ""
                viewState?.hideLoader()
                viewState?.showContactsScreen()
            }
            .subscribe { t1, t2 ->
                var kek = ""
                viewState?.hideLoader()
                viewState?.showContactsScreen()
            })
        //viewState?.showLoader()
    }

    fun login(email: String, password: String) {
        val success = MainApplication.getXmppConnection().login(
            email,
            password
        )
        if (success) {
            viewState?.showContactsScreen()
        } else {
            viewState?.createNewConnect()
            viewState?.showError("An error has occurred")
        }
    }
}