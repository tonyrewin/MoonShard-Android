package io.moonshard.moonshard.presentation.presenter

import io.moonshard.moonshard.LoginCredentials
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.ChatsHelper
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.presentation.view.ChatsView
import java9.util.concurrent.CompletableFuture
import java9.util.stream.StreamSupport
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.ArrayList
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.xdata.Form
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.jid.util.JidUtil
import org.jxmpp.jid.Jid
import java.lang.Exception


@InjectViewState
class ChatsPresenter : MvpPresenter<ChatsView>() {

    private val chatsHelper = ChatsHelper()

    fun setDialogs(){
        val dialogs = ArrayList<GenericDialog>()
        StreamSupport.stream(chatsHelper.loadLocalChats())
            .forEach { chatEntity -> dialogs.add(GenericDialog(chatEntity)) }
        StreamSupport.stream(dialogs)
            .forEach { dialog ->
                val messageEntity = LocalDBWrapper.getLastMessage(dialog.id)
                if (messageEntity != null) {
                    dialog.lastMessage = GenericMessage(messageEntity)
                }
            }

        viewState?.setData(dialogs)
        loadRemoteContactList()
    }

    fun createConference(){
        try {
            val manager = MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val jid = JidCreate.entityBareFrom("ploika" + "@conference." + MainApplication.getCurrentLoginCredentials().jabberHost)
            val muc = manager.getMultiUserChat(jid)
            val owners = JidUtil.jidSetFrom(arrayOf("just@moonshard.tech"))
            val nickName = Resourcepart.from("just")
            muc.create(nickName).makeInstant()
            muc.join(nickName)
        }catch (e:Exception){
            viewState?.showError(e.message!!)
        }


      //  muc.create(nickName)
      //  val form = muc.configurationForm.createAnswerForm()
      //  form.setAnswer("muc#roomconfig_roomowners", owners)
      //  muc.sendConfigurationForm(form)

        /*
        muc.create(nickName)
            .configFormManager
            .setRoomOwners(owners)
            .submitConfigurationForm()

         */
    }

    fun loadRemoteContactList() {
        CompletableFuture.supplyAsync {
            chatsHelper.remoteContacts
        }
            .thenAccept { contacts ->
                MainApplication.getMainUIThread().post {
                    if (contacts != null) {
                        StreamSupport.stream(contacts).forEach { contact ->
                            val chatID = contact.jid.asUnescapedString()
                            LocalDBWrapper.createChatEntry(
                                chatID,
                                if (contact.name == null) contact.jid.asUnescapedString().split(
                                    "@"
                                )[0] else contact.name,
                                arrayListOf()
                            )
                            val dialog = GenericDialog(LocalDBWrapper.getChatByChatID(chatID))
                            val messageEntity = LocalDBWrapper.getLastMessage(chatID)
                            if (messageEntity != null) {
                                dialog.lastMessage = GenericMessage(messageEntity)
                            }

                          //  dialogListAdapter.upsertItem(dialog)
                          //  dialogListAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }
}