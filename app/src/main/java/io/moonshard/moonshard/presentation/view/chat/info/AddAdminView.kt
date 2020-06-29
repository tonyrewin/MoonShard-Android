package io.moonshard.moonshard.presentation.view.chat.info

import moxy.MvpView
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

interface AddAdminView: MvpView {
    fun showError(error: String)

    @StateStrategyType(value = SkipStrategy::class)
    fun showChatScreen(jid:String)
}