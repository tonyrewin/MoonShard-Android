package io.moonshard.moonshard.presentation.presenter.settings

import android.graphics.BitmapFactory
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.settings.ProfileView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager


@InjectViewState
class ProfilePresenter : MvpPresenter<ProfileView>() {

    fun getInfoProfile() {
        try {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = card.nickName
            // val description = card.getField("DESCRIPTION")
            val description = card.middleName
            viewState?.setData(nickName,description)
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun getAvatar() {
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
}