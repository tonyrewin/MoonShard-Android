package io.moonshard.moonshard.presentation.presenter.create_group

import android.annotation.SuppressLint
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.create.CreateNewChatView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.activities.onboardregistration.VCardCustomManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*


@InjectViewState
class CreateNewChatPresenter : MvpPresenter<CreateNewChatView>() {

    @SuppressLint("CheckResult")
    fun createGroupChat(
        chatName: String,
        description: String
    ) {
        viewState?.showProgressBar()
        if (chatName.isNotBlank()) {
            val actualChatName: String
            val jidRoomString = UUID.randomUUID().toString()+"-chat" + "@conference.moonshard.tech"

            if (chatName.contains("@")) {
                viewState?.showToast("Вы ввели недопустимый символ")
                viewState?.hideProgressBar()
                return
            } else {
                actualChatName = chatName.split("@")[0]
            }

            try {
                val manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val entityBareJid = JidCreate.entityBareFrom(jidRoomString)
                val muc = manager.getMultiUserChat(entityBareJid)

                val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val card = vm.loadVCard()
                val nickName = Resourcepart.from(card.nickName)

                muc.create(nickName)
                // room is now created by locked
                val form = muc.configurationForm
                val answerForm = form.createAnswerForm()
                answerForm.setAnswer("muc#roomconfig_persistentroom", true)
                answerForm.setAnswer("muc#roomconfig_roomname", actualChatName)
                answerForm.setAnswer("muc#roomconfig_publicroom",true)
                answerForm.setAnswer("muc#roomconfig_roomdesc", description)
                val arrayList = arrayListOf<String>()
                arrayList.add("anyone")
                answerForm.setAnswer("muc#roomconfig_whois",arrayList)
                muc.sendConfigurationForm(answerForm)

                val vmMuc = VCardCustomManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val vCardMuc = VCard()
                vmMuc.saveVCard(vCardMuc, JidCreate.entityBareFrom(jidRoomString))

                val chatEntity = ChatEntity(
                    0,
                    jidRoomString,
                    actualChatName,
                    true,
                    0
                )

                ChatListRepository.addChat(chatEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        joinChat(jidRoomString)
                    }, {
                        it.message?.let { viewState?.showToast(it) }
                    })
            } catch (e: Exception) {
                viewState?.hideProgressBar()
                e.message?.let { viewState?.showToast(it) }
            }
        } else {
            viewState?.hideProgressBar()
            viewState?.showToast("Заполните поле")
        }
    }

    private fun joinChat(jid: String) {
        try {
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val entityBareJid = JidCreate.entityBareFrom(jid)
            val muc = manager.getMultiUserChat(entityBareJid)

            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard()
            val nickName = Resourcepart.from(card.nickName)

            muc.join(nickName)

            viewState?.hideProgressBar()
            viewState?.showChatScreen(jid)
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            e.message?.let { viewState?.showToast(it) }
        }
    }

}