package io.moonshard.moonshard.presentation.view.profile.history

import io.moonshard.moonshard.models.wallet.ListItem
import moxy.MvpView

interface HistoryTransactionView: MvpView {
    fun setData(transitions: List<ListItem>)
}