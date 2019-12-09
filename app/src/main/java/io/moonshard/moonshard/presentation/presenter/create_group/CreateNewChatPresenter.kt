package io.moonshard.moonshard.presentation.presenter.create_group

import android.annotation.SuppressLint
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.CreateNewChatView
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
import kotlin.collections.ArrayList

@InjectViewState
class CreateNewChatPresenter : MvpPresenter<CreateNewChatView>() {

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
        username: String, latitude: Float?, longitude: Float?,
        ttl: Int,
        category: String
    ) {
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

            //need  LocalDBWrapper.createChatEntry(actualUserName, actualUserName.split("@")[0], ArrayList<GenericUser>(), true)

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
    }

    private fun createRoomOnServer(
        latitude: Float?, longitude: Float?, ttl: Int, roomId: String,
        category: String
    ) {

        val myCategory = Category(1,"blabla")
        val categories = arrayListOf<Category>()
        categories.add(myCategory)
        if (latitude != null && longitude != null) {
            compositeDisposable.add(useCase!!.putRoom(latitude, longitude, ttl, roomId, categories)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { _, throwable ->
                    if (throwable == null) {
                        viewState?.showMapScreen()
                    }else{
                        viewState?.showToast("Ошибка: ${throwable.message}")
                    }
                })
        }
    }
}