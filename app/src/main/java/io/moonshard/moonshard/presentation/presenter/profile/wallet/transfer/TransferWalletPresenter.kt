package io.moonshard.moonshard.presentation.presenter.profile.wallet.transfer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.profile.wallet.transfer.TransferWalletView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import trikita.log.Log

@InjectViewState
class TransferWalletPresenter: MvpPresenter<TransferWalletView>() {

    fun showRecipient(jid:String){
        getInfoFromVCard(jid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setAvatar(jid,it["nickName"]!!)



                viewState?.setDataRecipient(it["nickName"]!!, it["status"]!!)
            }, {
                it.message?.let { it1 -> viewState?.showToast(it1) }
            })


    }

    private fun getInfoFromVCard(jid:String): Single<HashMap<String, String>> {
        return Single.create {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(JidCreate.entityBareFrom(jid))
            val nickName = card.nickName

            val hashMapData = hashMapOf<String, String>()
            hashMapData["nickName"] = nickName
            hashMapData["status"] = "online" //todo hardcore
            it.onSuccess(hashMapData)
        }
    }

    private fun setAvatar(jid: String, nameChat: String) {
        if (MainApplication.getCurrentChatActivity() != jid) {
            MainApplication.getXmppConnection().loadAvatar(jid, nameChat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bytes ->
                    val avatar: Bitmap?
                    if (bytes != null) {
                        avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                       viewState?.showAvatarRecipient(avatar)
                    }
                }, { throwable ->
                    Log.e(throwable.message)
                })
        }
    }
}