package io.moonshard.moonshard.presentation.presenter.settings

import com.google.gson.Gson
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.auth.response.ErrorResponse
import io.moonshard.moonshard.presentation.view.settings.SecurityView
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.iqregister.AccountManager
import retrofit2.HttpException

@InjectViewState
class SecurityPresenter : MvpPresenter<SecurityView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = AuthUseCase()
    }


    fun changePassword(
        newPass: String, repeatNewPass: String, currentPass: String
    ) {
        /*
        if (newPass == repeatNewPass) {
            changeNewPassword(newPass)
        } else {
            viewState?.showError("Пароли не совпадают")
        }

         */


        if (SecurePreferences.getStringValue("pass", "") == currentPass) {
            if (newPass == repeatNewPass) {
               // changeNewPassword(newPass)
            } else {
                viewState?.showError("Пароли не совпадают")
            }
        } else {
            viewState?.showError("Текущий пароль неверный")
        }
    }
/*
    private fun changeNewPassword(newPass: String) {
        compositeDisposable.add(useCase!!.changePassword(
            jid, password
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->

            })
    }

 */
}