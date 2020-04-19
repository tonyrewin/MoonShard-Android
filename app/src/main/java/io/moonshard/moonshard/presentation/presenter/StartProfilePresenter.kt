package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.StartProfileView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.vcardtemp.VCardManager


@InjectViewState
class StartProfilePresenter : MvpPresenter<StartProfileView>() {

    fun setData(nickName: String, bytes: ByteArray?, mimeType: String?) {

        if(nickName.isEmpty()){
            // TODO: move this code to StartProfileView
            viewState?.showError("Enter a nickname")
            return
        }

        try {
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            card.nickName = nickName
            if (bytes != null && mimeType != null) {
                card.setAvatar(bytes, mimeType)
            }
            vm.saveVCard(card)
            viewState?.showContactsScreen()
        } catch (e: Exception) {
            e.message?.let {
                viewState?.showError(it)
            }
        }
    }

    fun getNickName():String{
      return MainApplication.getJid().split("@")[0]
    }
}