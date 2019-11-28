package io.moonshard.moonshard.presentation.presenter.create_group

import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.helpers.LocalDBWrapper
import io.moonshard.moonshard.models.jabber.GenericUser
import io.moonshard.moonshard.presentation.view.CreateNewChatView
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

@InjectViewState
class CreateNewChatPresenter : MvpPresenter<CreateNewChatView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun createGroupChat(
        username: String, latitude: Float?, longitude: Float?,
        ttl: Int,
        category: String
    ) {
        val actualUserName: String

        if (username.contains("@")) {
            viewState?.showToast("Вы ввели недопустимый символ")
            return
        } else {
            actualUserName = username.split("@")[0] + "@conference.moonshard.tech"
        }

        try {
            val manager =
                MultiUserChatManager.getInstanceFor(MainApplication.getXmppConnection().connection)
            val entityBareJid = JidCreate.entityBareFrom(actualUserName)
            val muc = manager.getMultiUserChat(entityBareJid)
            val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)

            muc.create(nickName)
            // room is now created by locked
            val form = muc.configurationForm
            val answerForm = form.createAnswerForm()
            answerForm.setAnswer("muc#roomconfig_persistentroom", true)
            muc.sendConfigurationForm(answerForm)

            LocalDBWrapper.createChatEntry(
                actualUserName, actualUserName.split("@")[0],
                ArrayList<GenericUser>(), true
            )

            createRoomOnServer(latitude, longitude, ttl, actualUserName, category)
        } catch (e: Exception) {
            e.message?.let { viewState?.showToast(it) }
        }
    }

    private fun createRoomOnServer(
        latitude: Float?, longitude: Float?, ttl: Int, roomId: String,
        category: String
    ) {
        if (latitude != null && longitude != null) {
            compositeDisposable.add(useCase!!.putRoom(latitude, longitude, ttl, roomId, category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError {
                    viewState?.showToast("Ошибка: ${it.message}")
                }
                .subscribe { t1, t2 ->
                    if (t1 != null) {
                        viewState?.showMapScreen()
                    }
                })
        }
    }
}