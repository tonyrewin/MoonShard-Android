package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.api.Category
import moxy.MvpView

interface CreateNewEventView: MvpView {
    fun showToast(text: String)
    fun showMapScreen()
    fun showCategories(categories:ArrayList<Category>)
    fun showProgressBar()
    fun hideProgressBar()
}