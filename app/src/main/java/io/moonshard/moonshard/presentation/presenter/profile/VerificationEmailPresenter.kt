package io.moonshard.moonshard.presentation.presenter.profile

import android.util.Log
import com.google.gson.Gson
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.getLongStringValue
import io.moonshard.moonshard.models.api.auth.response.Error
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.profile.VerificationEmailView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException


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
                            MainApplication.initWalletLibrary()
                            viewState?.showVerificationLayout(true)
                        } else {
                            viewState?.showVerificationLayout(false)
                        }
                    }
                    com.orhanobut.logger.Logger.d(result)
                } else {
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showToast(myError.error.message)
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
                        val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                        val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                        viewState?.showToast(myError.error.message)
                    }
                })
        }else{
            viewState?.showToast("Введите Email")
        }
    }
}