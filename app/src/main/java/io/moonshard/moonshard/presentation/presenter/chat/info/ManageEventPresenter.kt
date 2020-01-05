package io.moonshard.moonshard.presentation.presenter.chat.info

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.presentation.view.chat.ManageChatView
import io.moonshard.moonshard.presentation.view.chat.info.ManageEventView
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate

@InjectViewState
class ManageEventPresenter : MvpPresenter<ManageEventView>() {

    fun setNewNameChat(name:String,jid: String){
        try {
            val groupId = JidCreate.entityBareFrom(jid)
            val muc =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                    .getMultiUserChat(groupId)

            //  val resourcepartNickname = JidCreate.bareFrom(name+"@conference.moonshard.tech").resourceOrEmpty
            // muc.changeNickname(Resourcepart.from(name))
        } catch (e: Exception) {
            val test = ""
            // e.message?.let { viewState?.showToast(it) }
        }
    }

    fun setAvatar(jid: String,bytes: ByteArray?, mimeType: String?){
        val manager = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
        try {
            val card = manager.loadVCard(JidCreate.entityBareFrom(jid))
            if (bytes != null && mimeType != null) {
                card.setAvatar(bytes, mimeType)
            }
            manager.saveVCard(card)
        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}