package io.moonshard.moonshard.presentation.view.chat

import io.moonshard.moonshard.models.api.RoomPin
import moxy.MvpView

interface EventsView: MvpView {
    fun showError(error: String)
    fun setEvents(events:ArrayList<RoomPin>)
}