package io.moonshard.moonshard.presentation.presenter.chat.info

import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.ManageChatView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.ui.activities.onboardregistration.VCardCustomManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.Occupant
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate
import java.util.*
import kotlin.Comparator


@InjectViewState
class ManageChatPresenter : MvpPresenter<ManageChatView>() {

    fun getDataInfo(jid: String) {
        try {
            viewState?.showProgressBar()
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(jid))

            val info =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getRoomInfo(muc.room)

            viewState?.showName(info.name)
            viewState?.showDescription(info.description)
            viewState?.showOccupantsCount(info.occupantsCount.toString())
            viewState?.showAdminsCount(removeDuplicates(muc.moderators).size.toString())
            viewState?.hideProgressBar()
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            viewState?.showToast("Произошла ошибка")
            Logger.d(e)
        }
    }

    fun setData(
        name: String,
        description: String,
        jid: String,
        bytes: ByteArray?,
        mimeType: String?
    ) {
        try {
            viewState?.showProgressBar()
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(jid))

            ChatListRepository.getChatByJidSingle(JidCreate.from(jid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.chatName = name
                    setAvatarServer(muc,bytes,mimeType)
                    changeDescription(muc, description)
                    changeChatNameServer(muc, it)
                }, {

                })
        } catch (e: Exception) {
            viewState?.hideProgressBar()
            e.message?.let { viewState.showToast(it) }
        }
    }

    private fun changeChatNameServer(muc: MultiUserChat, chat: ChatEntity) {
        try {
            val form = muc.configurationForm
            val answerForm = form.createAnswerForm()
            answerForm.setAnswer("muc#roomconfig_roomname", chat.chatName)
            muc.sendConfigurationForm(answerForm)
            changeChatNameBaseDate(chat)
        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }

    private fun setAvatarServer(muc: MultiUserChat, bytes: ByteArray?, mimeType: String?) {
        try {
            val vm = VCardCustomManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCardMuc(muc.room)
            card.setAvatar(bytes,mimeType)
            vm.saveVCard(card,muc.room)
        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }

    private fun changeChatNameBaseDate(chat: ChatEntity) {
        ChatListRepository.changeChatName(chat)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState?.hideProgressBar()
                viewState.showChatInfo()
            }, {
                viewState?.hideProgressBar()
                Logger.d(it)
            })
    }

    private fun changeDescription(muc: MultiUserChat, description: String) {
        try {
            val form = muc.configurationForm
            val answerForm = form.createAnswerForm()
            answerForm.setAnswer("muc#roomconfig_roomdesc", description)
            muc.sendConfigurationForm(answerForm)
        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }

    fun destroyRoom(jid: String) {
        try {
            val muc =
                MainApplication.getXmppConnection().multiUserChatManager
                    .getMultiUserChat(JidCreate.entityBareFrom(jid))
            val myJid = MainApplication.getXmppConnection().jid.asUnescapedString()
            val roomJid = JidCreate.entityBareFrom(jid)
            muc.destroy(myJid, roomJid)
            viewState?.showChatsScreen()
        } catch (e: Exception) {
            Logger.d(e)
            viewState?.showToast("Произошла ошибка на сервере")
        }
    }
    private fun removeDuplicates(list: List<Occupant>?): ArrayList<Occupant> {
        val s: MutableSet<Occupant> = TreeSet<Occupant>(Comparator<Occupant?> { o1, o2 ->
            if(o1!!.jid.asUnescapedString().split("/")[0]==o2!!.jid.asUnescapedString().split("/")[0]){
                0
            }else{
                1
            }
        })
        s.addAll(list!!)

        val newArray = arrayListOf<Occupant>()
        newArray.addAll(s)
        return newArray
    }
}