package io.moonshard.moonshard.presentation.presenter.create_group

import io.moonshard.moonshard.presentation.view.AddChatView
import moxy.InjectViewState
import moxy.MvpPresenter


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