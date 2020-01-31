package io.moonshard.moonshard.presentation.presenter


import com.orhanobut.logger.Logger
import io.moonshard.moonshard.API
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.LoginView
import io.moonshard.moonshard.repository.ChatListRepository
import io.moonshard.moonshard.usecase.TestUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject


@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>() {

    fun addSupportChat(){
        val chatEntity = ChatEntity(
            0,
            "support@conference.moonshard.tech",
            "Чат поддержки",
            true,
            0
        )

        ChatListRepository.addChat(chatEntity)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {
                Logger.d(it)
            })

    }

}