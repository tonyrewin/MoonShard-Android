package io.moonshard.moonshard.presentation.presenter


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.orhanobut.logger.Logger
import de.adorsys.android.securestoragelibrary.SecurePreferences
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.SettingsView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.repository.ChatUserRepository
import io.moonshard.moonshard.repository.MessageRepository
import io.moonshard.moonshard.usecase.AuthUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager

@InjectViewState
class SettingsPresenter : MvpPresenter<SettingsView>() {

    private var useCase: AuthUseCase? = null
    private val compositeDisposable = CompositeDisposable()


    init {
        useCase = AuthUseCase()
    }

    /*
    fun logOut() {
        Thread {
            val success = MainApplication.getXmppConnection().logOut()
            clearBaseDate()
            if (success) {
                MainApplication.getMainUIThread().post {
                    viewState?.showRegistrationScreen()
                }
            } else {
                MainApplication.getMainUIThread().post {
                    viewState?.showError("Error")
                }
            }
        }.start()
    }
     */

    fun newLogOut(){
        compositeDisposable.add(useCase!!.logout(
            MainApplication.getCurrentLoginCredentials().accessToken, MainApplication.getCurrentLoginCredentials().refreshToken
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, throwable ->
                if (throwable == null) {
                    MainApplication.resetLoginCredentials()
                    MainApplication.getXmppConnection().setStatus(false, "OFFLINE")
                    clearBaseDate()
                } else {
                    viewState?.showError(throwable.localizedMessage)
                }
            })
    }

    private fun clearBaseDate() {
        Completable.mergeArray(
            ChatListRepository.clearChats(),
            ChatUserRepository.clearUsers(),
            MessageRepository.clearMessages()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.showRegistrationScreen()
            }, {
                Logger.d(it)
            })
    }

    fun disableInviteInChats(){
        MainApplication.getXmppConnection().disableInviteInChats()
        SecurePreferences.setValue("inviteInChats", false)
    }

    fun enableInviteInChats() {
        MainApplication.getXmppConnection().enableInviteInChats()
        SecurePreferences.setValue("inviteInChats", true)
    }
}