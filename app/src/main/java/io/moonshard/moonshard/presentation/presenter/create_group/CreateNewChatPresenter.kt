package io.moonshard.moonshard.presentation.presenter.create_group

import android.annotation.SuppressLint
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.create.CreateNewChatView
import io.moonshard.moonshard.repository.ChatListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*


@InjectViewState
class CreateNewChatPresenter : MvpPresenter<CreateNewChatView>() {


    @SuppressLint("CheckResult")
    fun createGroupChat(
        username: String
    ) {
        if (username.isNotBlank()) {
            val actualUserName: String
            val jidRoomString = UUID.randomUUID().toString() + "@conference.moonshard.tech"

            if (username.contains("@")) {
                viewState?.showToast("Вы ввели недопустимый символ")
                return
            } else {
                actualUserName = username.split("@")[0]
            }

            try {
                val manager =
                    MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
                val entityBareJid = JidCreate.entityBareFrom(jidRoomString)
                val muc = manager.getMultiUserChat(entityBareJid)
                val nickName =
                    Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)

                muc.create(nickName)
                // room is now created by locked
                val form = muc.configurationForm
                val answerForm = form.createAnswerForm()
                answerForm.setAnswer("muc#roomconfig_persistentroom", true)
                answerForm.setAnswer("muc#roomconfig_roomname", actualUserName)
                muc.sendConfigurationForm(answerForm)

                val chatEntity = ChatEntity(
                    0,
                    jidRoomString,
                    actualUserName,
                    true,
                    0
                )

                ChatListRepository.addChat(chatEntity)
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        viewState?.showChatsScreen()
                    }, {
                        it.message?.let { viewState?.showToast(it) }
                    })
            } catch (e: Exception) {
                e.message?.let { viewState?.showToast(it) }
            }
        } else {
            viewState?.showToast("Заполните поле")
        }
    }
}