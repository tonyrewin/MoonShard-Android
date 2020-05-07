package io.moonshard.moonshard.presentation.presenter


import com.google.gson.Gson
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.common.setLongStringValue
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.RegisterView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException


@InjectViewState
class RegisterPresenter : MvpPresenter<RegisterView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = AuthUseCase()
    }

    fun registerOnServer(jid: String, password: String) {
        compositeDisposable.add(useCase!!.register(
            jid, password
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    saveLoginCredentials(jid,password)
                    viewState?.successRegistration()
                    login()
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showToast(myError.error.message)
                }
                viewState?.hideLoader()
            })
    }

    private fun saveLoginCredentials(email: String, password: String) {
        SecurePreferences.setValue("jid", email)
        SecurePreferences.setValue("pass", password)
    }

    fun login() {
        val nickname = SecurePreferences.getStringValue("jid", null)
        val password = SecurePreferences.getStringValue("pass", null)

        compositeDisposable.add(useCase!!.login(
            nickname!!, password!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    saveLoginCredentials(
                        nickname,
                        password,
                        result.accessToken,
                        result.refreshToken
                    )
                    viewState?.startService()
                    Logger.d(result)
                } else {
                    viewState?.hideLoader()
                    viewState?.setRegisterLayout()
                    Logger.d(result)
                }
            })
    }

    private fun saveLoginCredentials(
        email: String,
        password: String,
        accessToken: String,
        refreshToken: String
    ) {
        SecurePreferences.setValue("jid", email)
        SecurePreferences.setValue("pass", password)
        setLongStringValue("accessToken", accessToken)
        setLongStringValue("refreshToken", refreshToken)
    }
}