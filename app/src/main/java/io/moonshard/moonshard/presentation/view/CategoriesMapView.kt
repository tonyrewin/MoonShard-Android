package io.moonshard.moonshard.presentation.view

import io.moonshard.moonshard.models.api.Category
import moxy.MvpView

interface CategoriesMapView: MvpView {
    fun showCategories(categories:ArrayList<Category>)
}