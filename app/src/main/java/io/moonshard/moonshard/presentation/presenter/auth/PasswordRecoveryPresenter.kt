package io.moonshard.moonshard.presentation.presenter.auth

import android.text.Editable
import com.google.gson.Gson
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
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
                    viewState?.showError("Письмо с инструкцией по восстановлению пароля отправлено на вашу почту")
                    viewState?.back()
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showError(myError.error.message)
                    com.orhanobut.logger.Logger.d(result)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}