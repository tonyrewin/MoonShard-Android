package io.moonshard.moonshard.presentation.presenter.settings

import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.settings.ChangeProfileView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import java.lang.Exception

@InjectViewState
class ChangeProfilePresenter : MvpPresenter<ChangeProfileView>() {

    fun setData(nickName: String, description: String,bytes: ByteArray?, mimeType: String?) {
        try {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            card.nickName = nickName
            if(bytes!=null && mimeType!=null){
                card.setAvatar(bytes,mimeType)
            }
            //card.setField("DESCRIPTION",description)
            card.middleName = description
            vm.saveVCard(card)
            viewState?.showProfile()
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun getInfoProfile() {
        val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
        val card = vm.loadVCard()
        val nickName = card.nickName
       // val description = card.getField("DESCRIPTION")
        val description = card.middleName
        viewState?.setData(nickName, description)
    }

    fun getAvatar() {
        val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
        val card = vm.loadVCard()
        val avatarBytes = card.avatar
        avatarBytes?.let {
            val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
            viewState?.setAvatar(bitmap)
        }
    }


    fun setAvatar(bytes: ByteArray, mimeType: String) {
        try {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            card.setAvatar(bytes,mimeType)
            vm.saveVCard(card)
        }catch (e:Exception){

        }
    }
}