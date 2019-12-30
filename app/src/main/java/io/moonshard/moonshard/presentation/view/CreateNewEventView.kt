package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import moxy.MvpView

interface CreateNewEventView: MvpView {
    fun showToast(text: String)
    fun showMapScreen()
    fun showCategories(categories:ArrayList<Category>)
    fun showAdminChats(chats:ArrayList<ChatEntity>)
    fun showProgressBar()
    fun hideProgressBar()
}