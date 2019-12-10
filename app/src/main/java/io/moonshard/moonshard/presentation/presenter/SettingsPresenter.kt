package io.moonshard.moonshard.presentation.presenter


import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.presentation.view.SettingsView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import java.lang.Exception

@InjectViewState
class SettingsPresenter : MvpPresenter<SettingsView>() {

    fun logOut(){
       val success =  MainApplication.getXmppConnection().logOut()
        if(success){
            viewState?.showRegistrationScreen()
        }else{
            viewState?.showError("Error")
        }
    }

    fun getAvatar(){
        try {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val avatarBytes = card.avatar
            avatarBytes?.let {
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                viewState?.setAvatar(bitmap)
            }
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun getName(){
        val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
        val card = vm.loadVCard()
        val nickName = card.nickName
        val jidPart =  card.to.asBareJid().localpartOrNull.toString()
        viewState?.setData(nickName,jidPart)
    }
}