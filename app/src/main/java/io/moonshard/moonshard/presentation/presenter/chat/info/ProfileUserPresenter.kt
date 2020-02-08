package io.moonshard.moonshard.presentation.presenter.chat.info

import android.graphics.BitmapFactory
import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.common.NotFoundException
import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.chat.info.ProfileUserView
import io.moonshard.moonshard.repository.ChatListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate


@InjectViewState
class ProfileUserPresenter: MvpPresenter<ProfileUserView>() {

    fun getInfoProfile(jid:String?) {
        try {
            val jidUser = JidCreate.entityBareFrom(jid)
            val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val card = vm.loadVCard(jidUser)
            var nickName=""
            nickName = if(card.nickName.isNullOrBlank()){
                card.to.asBareJid().localpartOrNull.toString()
            }else{
                card.nickName
            }
            // val description = card.getField("DESCRIPTION")
            val description = card.middleName
            viewState?.setData(nickName,description)
        }catch (e:Exception){
            e.message?.let { viewState?.showError(it) }
        }
    }

    fun startChatWithUser(jid: String) {
        try {
                val jidUser = JidCreate.entityBareFrom(jid)
                val vm = VCardManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val card = vm.loadVCard(jidUser)
                val nickName: String
                nickName = if (card.nickName.isNullOrBlank()) {
                    card.to.asBareJid().localpartOrNull.toString()
                } else {
                    card.nickName
                }

            val chatEntity = ChatEntity(
                jid = jid,
                chatName = nickName,
                isGroupChat = false,
                unreadMessagesCount = 0
            )

            ChatListRepository.getChatByJid(JidCreate.from(jid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState?.showChatScreen(jid)
                }, {
                    if(it is NotFoundException) {

                        ChatListRepository.addChat(chatEntity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                createChatOneToOne(jid)
                            }, { throwable ->
                                Logger.d(throwable.message)
                            })
                    }
                })
        } catch (e: Exception) {
            e.message?.let { viewState?.showError(it) }
        }
    }

    private fun createChatOneToOne(toJid: String) {
        try {
            val jid = JidCreate.from(toJid)
            val chat = ChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            chat.chatWith(jid.asEntityBareJidIfPossible())
            ChatRepository.idChatCurrent = toJid
            viewState?.showChatScreen(toJid)
        } catch (e: Exception) {
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