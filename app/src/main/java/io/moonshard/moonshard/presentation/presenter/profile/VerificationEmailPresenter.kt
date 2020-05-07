package io.moonshard.moonshard.presentation.presenter.profile

import com.google.gson.Gson
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


    fun verificationEmail(email:String){
        if(email.isNotEmpty()){
            val accessToken = getLongStringValue("accessToken")

            compositeDisposable.add(useCase!!.addEmailToProfile(
                email,accessToken!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, throwable ->
                    if (throwable == null) {
                        viewState?.showToast("Письмо было успешно отправлено на вашу почту.")
                        viewState?.back()
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