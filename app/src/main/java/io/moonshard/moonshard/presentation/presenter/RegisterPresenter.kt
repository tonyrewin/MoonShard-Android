package io.moonshard.moonshard.presentation.presenter


import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter


@InjectViewState
class RegisterPresenter : MvpPresenter<RegisterView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = AuthUseCase()
    }

    fun register(email: String, pass: String) {
        viewState.showLoader()
        MainApplication.getXmppConnection()?.register(email, pass)
    }

    fun registerOnServer(nickname: String,password: String){
        compositeDisposable.add(useCase!!.register(
            nickname,password
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable != null) {
                    com.orhanobut.logger.Logger.d(result)
                } else {
                    com.orhanobut.logger.Logger.d(result)
                }
            })
    }
}