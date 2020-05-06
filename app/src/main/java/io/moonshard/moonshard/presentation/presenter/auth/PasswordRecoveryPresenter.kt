package io.moonshard.moonshard.presentation.presenter.auth

import android.text.Editable
import io.moonshard.moonshard.presentation.view.auth.PasswordRecoveryView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException
import zlc.season.rxdownload4.utils.log
import java.util.logging.Logger

@InjectViewState
class PasswordRecoveryPresenter: MvpPresenter<PasswordRecoveryView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()


    init {
        useCase = AuthUseCase()
    }

    fun recoveryPassword(email: String, password: String) {
        compositeDisposable.add(useCase!!.resetPassword(
           email,password
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    com.orhanobut.logger.Logger.d(result)
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    com.orhanobut.logger.Logger.d(result)
                }
            })
    }

    fun savePrivateKey(encryptionPassword:String,token:String) {
        compositeDisposable.add(useCase!!.refreshToken(
            encryptionPassword,token
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    com.orhanobut.logger.Logger.d(result)
                } else {
                    com.orhanobut.logger.Logger.d(result)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}