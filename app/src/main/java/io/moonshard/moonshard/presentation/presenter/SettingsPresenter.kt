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
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager

@InjectViewState
class SettingsPresenter : MvpPresenter<SettingsView>() {

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

    private fun clearBaseDate() {
        Completable.mergeArray(
            ChatListRepository.clearChats(),
            ChatUserRepository.clearUsers(),
            MessageRepository.clearMessages()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

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