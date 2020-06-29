package io.moonshard.moonshard.presentation.presenter.profile

import android.util.Log
import com.example.moonshardwallet.MainService
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.profile.VerificationEmailView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException
import java.net.UnknownHostException


@InjectViewState
class VerificationEmailPresenter: MvpPresenter<VerificationEmailView>() {
    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()


    init {
        useCase = AuthUseCase()
    }


    fun getVerificationEmail() {
        // val accessToken = getLongStringValue("accessToken")

        val accessToken = MainApplication.getCurrentLoginCredentials().accessToken

        Log.d("myTimeUserProfile",accessToken)
        compositeDisposable.add(useCase!!.getUserProfileInfo(accessToken!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                Log.d("myTimeUserProfile","kek")
                if (throwable == null) {
                    if (result.email.isNullOrBlank()) {
                        viewState?.showVerificationLayout(false)
                    } else {
                        if (result.isActivated!!) {
                            getPrivateKey()
                            viewState?.showVerificationLayout(true)
                        } else {
                            viewState?.showVerificationLayout(false)
                        }
                    }
                    Logger.d(result)
                } else {
                    when (throwable) {
                        is UnknownHostException -> {
                            viewState?.showToast("Ошибка интернет-соединения")
                        }
                        is HttpException -> {
                            val jsonError = throwable.response()?.errorBody()?.string()
                            val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                            viewState?.showToast(myError.error.message)
                        }
                        else -> {
                            viewState?.showToast("Произошла ошибка")
                        }
                    }
                }
            })
    }

    fun getPrivateKey() {
        val accessToken = getLongStringValue("accessToken")

        compositeDisposable.add(useCase!!.getPrivateKey(
            accessToken!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    if (result.privateKey.isNotBlank()) {
                        MainApplication.initWalletLibrary(result.privateKey)
                    }
                } else {
                    if(throwable is UnknownHostException){
                        viewState?.showToast("Ошибка интернет-соединения")
                    }else if(throwable is HttpException){
                        val jsonError = throwable.response()!!.errorBody()!!.string()
                        val (error) = Gson().fromJson(jsonError, ErrorResponse::class.java)

                        if (error.message == "cipher text too short") {
                            MainApplication.initWalletLibrary(null)
                        }
                    }else{
                        viewState?.showToast("Произошла ошибка")
                    }
                    Logger.d(result)
                }
            })
    }

    fun verificationEmail(email:String){
        if(email.isNotBlank()){
            val accessToken = getLongStringValue("accessToken")

            compositeDisposable.add(useCase!!.addEmailToProfile(
                email,accessToken!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, throwable ->
                    if (throwable == null) {
                        viewState?.showToast("Письмо отправлено")
                    } else {
                        when (throwable) {
                            is UnknownHostException -> {
                                viewState?.showToast("Ошибка интернет-соединения")
                            }
                            is HttpException -> {
                                val jsonError = throwable.response()?.errorBody()?.string()
                                val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                                when (myError.error.message) {
                                    "user with such email already exists" -> {
                                        viewState?.showToast("Пользователь с таким email уже существует")
                                    }
                                    "incorrect format of email" -> {
                                        viewState?.showToast("Некорректный формат почты")
                                    }
                                    else -> {
                                        viewState?.showToast("Произошла ошибка")
                                    }
                                }
                            }
                            else -> {
                                viewState?.showToast("Произошла ошибка")
                            }
                        }
                        Logger.d(throwable)
                            //viewState?.showToast(myError.error.message)
                    }
                })
        }else{
            viewState?.showToast("Введите Email")
        }
    }
}