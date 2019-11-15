package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.GenericMessage
import moxy.MvpView

interface ChatView: MvpView {


    fun addMessage(message: GenericMessage)
}