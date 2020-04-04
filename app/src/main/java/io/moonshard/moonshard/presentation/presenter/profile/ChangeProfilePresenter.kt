package io.moonshard.moonshard.presentation.presenter.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.profile.ChangeProfileView
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager

@InjectViewState
class ChangeProfilePresenter : MvpPresenter<ChangeProfileView>() {

    fun setData(nickName: String, description: String, bytes: ByteArray?, mimeType: String?) {
        viewState?.showProgressBar()
        setDataInVCard(nickName,description,bytes,mimeType).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.hideProgressBar()
                viewState?.showProfile()
            }, {
                viewState?.hideProgressBar()
                it.message?.let { it1 -> viewState?.showError(it1) }
            })
    }

    private fun setDataInVCard(nickName: String, description: String, bytes: ByteArray?, mimeType: String?):Completable {
        return Completable.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            card.nickName = nickName
            if (bytes != null && mimeType != null) {
                card.setAvatar(bytes, mimeType)
            }
            //card.setField("DESCRIPTION",description)
            card.middleName = description
            vm.saveVCard(card)
            it.onComplete()
        }
    }

    fun getInfoProfile() {
        viewState?.showProgressBar()
        getInfoFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.setData(it["nickName"], it["description"])
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
            it.onNext(hashMapData)
        }
    }

    fun getAvatar() {
        getAvatarFromVCard().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.hideProgressBar()
                viewState?.setAvatar(it)
            }, {
                viewState?.hideProgressBar()
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
            }else{
                it.onError(Throwable())
            }
        }
    }
}