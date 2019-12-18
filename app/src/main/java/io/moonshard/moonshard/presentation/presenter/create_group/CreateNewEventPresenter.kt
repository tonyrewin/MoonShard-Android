package io.moonshard.moonshard.presentation.presenter.create_group

import android.annotation.SuppressLint
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.CreateNewEventView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.usecase.RoomsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.*

@InjectViewState
class CreateNewEventPresenter : MvpPresenter<CreateNewEventView>() {

    private var useCase: RoomsUseCase? = null
    private val compositeDisposable = CompositeDisposable()

    init {
        useCase = RoomsUseCase()
    }

    fun getCategories(){
        viewState?.showProgressBar()
        compositeDisposable.add(useCase!!.getCategories()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { categories, throwable ->
                viewState?.hideProgressBar()
                if (throwable == null) {
                    viewState?.showCategories(categories)
                }else{
                    viewState?.showToast("Ошибка: ${throwable.message}")
                }
            })
    }

    @SuppressLint("CheckResult")
    fun createGroupChat(
        username: String, latitude: Double?, longitude: Double?,
        ttl: Int,
        category: Category?
    ) {

        if (latitude != null && longitude != null && category!=null) {
            val actualUserName: String
            val jidRoomString  = UUID.randomUUID().toString()+ "@conference.moonshard.tech"

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
                val nickName = Resourcepart.from(MainApplication.getCurrentLoginCredentials().username)

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
                    .subscribe {
                        createRoomOnServer(latitude, longitude, ttl, jidRoomString, category)
                    }
            } catch (e: Exception) {
                e.message?.let { viewState?.showToast(it) }
            }
        }else{
            viewState?.showToast("Заполните поля")
        }
    }

    private fun createRoomOnServer(
        latitude: Double?, longitude: Double?, ttl: Int, roomId: String,
        category: Category
    ) {
        val categories = arrayListOf<Category>()
        categories.add(category)
            compositeDisposable.add(useCase!!.putRoom(latitude, longitude, ttl, roomId, categories)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { _, throwable ->
                    if (throwable == null) {
                        viewState?.showMapScreen()
                        ChooseChatRepository.clean()
                    }else{
                        viewState?.showToast("Ошибка: ${throwable.message}")
                    }
                })

    }
}