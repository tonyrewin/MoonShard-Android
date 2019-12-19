package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.info.ProfileUserView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate


@InjectViewState
class ProfileUserPresenter: MvpPresenter<ProfileUserView>() {



    fun getInfoProfile(jid:String?) {
        try {
            val jidUser = JidCreate.entityBareFrom(jid)
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(jidUser)
            val nickName = card.nickName
            // val description = card.getField("DESCRIPTION")
            val description = card.middleName
            viewState?.setData(nickName,description)
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun getAvatar(jid:String?) {
        try {
            val jidUser = JidCreate.entityBareFrom(jid)
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(jidUser)
            val avatarBytes = card.avatar
            avatarBytes?.let {
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                viewState?.setAvatar(bitmap)
            }
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }
}