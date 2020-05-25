package io.moonshard.moonshard.presentation.presenter


import com.google.gson.Gson
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.common.setLongStringValue
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException


@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = AuthUseCase()
    }

    fun addSupportChat(){
        /*
        val chatEntity = ChatEntity(
            0,
            "support@conference.moonshard.tech",
            "Чат поддержки",
            true,
            0
        )

        ChatListRepository.addChat(chatEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                Logger.d(it)
            })
         */

    }

    fun login(nickname:String,password:String){
        compositeDisposable.add(useCase!!.login(
            nickname,password
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    saveLoginCredentials(nickname, password,result.accessToken,result.refreshToken)
                    viewState?.startService()
                    Logger.d(result)
                } else {
                    Logger.d(result)
                    val jsonError = (throwable as HttpException).response()?.errorBody()?.string()
                    val myError = Gson().fromJson(jsonError, ErrorResponse::class.java)
                    viewState?.showError(myError.error.message)
                    viewState?.hideLoader()
                }
            })
    }

    private fun saveLoginCredentials(email: String, password: String,accessToken:String,refreshToken:String) {
        SecurePreferences.setValue("jid", email)
        SecurePreferences.setValue("pass", password)
        setLongStringValue("accessToken", accessToken)
        setLongStringValue("refreshToken",refreshToken)
    }
}