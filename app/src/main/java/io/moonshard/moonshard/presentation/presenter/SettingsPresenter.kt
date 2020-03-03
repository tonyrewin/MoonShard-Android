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

    fun getAvatar() {
        getAvatarBitmap().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setAvatar(it)
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getAvatarBitmap(): Observable<Bitmap> {
        return Observable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val avatarBytes = card.avatar

            if (avatarBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                it.onNext(bitmap)
            } else {
                it.onError(Throwable())
            }
        }
    }

    fun getName() {
        getNameFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setData(it["nickName"], it["jidPart"])
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getNameFromVCard(): Observable<HashMap<String, String>> {
        return Observable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = card.nickName
            val jidPart = card.to.asBareJid().localpartOrNull.toString()
            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["jidPart"] = jidPart
            it.onNext(hashMapData)
        }
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
        MainApplication.getXmppConnection().disableInvitionInChats()
        SecurePreferences.setValue("inviteInChats", false)
    }

    fun enableInvitionInChats() {
        MainApplication.getXmppConnection().enableInvitionInChats()
        SecurePreferences.setValue("inviteInChats", true)
    }
}