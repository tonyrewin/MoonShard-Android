package io.moonshard.moonshard.presentation.presenter.create_group

import com.orhanobut.logger.Logger
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.view.AddChatView
import io.moonshard.moonshard.repository.ChatListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smackx.vcardtemp.VCardManager
import org.jxmpp.jid.impl.JidCreate


@InjectViewState
class AddChatPresenter : MvpPresenter<AddChatView>() {

    fun startChatWithPeer(username: String) {
        if (!username.contains("@")) {
            viewState?.showError("Должен содержать @ host")
            return
        }
        //need  LocalDBWrapper.createChatEntry(username, username.split("@")[0], ArrayList<GenericUser>(),false)
        viewState?.showCreateNewChatScreen()
    }
}