package io.moonshard.moonshard.presentation.presenter.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.settings.ProfileView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager


@InjectViewState
class ProfilePresenter : MvpPresenter<ProfileView>() {

    fun getInfoProfile() {
        getInfoFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setData(it["nickName"], it["description"],it["jidPart"])
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getInfoFromVCard(): Observable<HashMap<String, String>> {
        return Observable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = card.nickName
            // val description = card.getField("DESCRIPTION")
            val description = card.middleName
            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["description"] = description
            hashMapData["jidPart"] = card.to.asBareJid().localpartOrNull.toString()
            it.onNext(hashMapData)
        }
    }

    fun getAvatar() {
        getAvatarFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setAvatar(it)
            }, {
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun getAvatarFromVCard(): Observable<Bitmap> {
        return Observable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val avatarBytes = card.avatar

            if (avatarBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                it.onNext(bitmap)
            }
        }
    }
}

