package io.moonshard.moonshard.presentation.presenter


import android.content.Context
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.setLongStringValue
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.usecase.AuthUseCase
import io.moonshard.moonshard.usecase.TestUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject


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