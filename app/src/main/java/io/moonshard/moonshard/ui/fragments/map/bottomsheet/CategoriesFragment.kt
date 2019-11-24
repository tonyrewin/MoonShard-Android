package io.moonshard.moonshard.ui.fragments.map.bottomsheet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.presentation.view.CategoriesMapView
import io.moonshard.moonshard.ui.adapters.CategoryMapAdapter
import io.moonshard.moonshard.ui.adapters.CategoryMapListener
import kotlinx.android.synthetic.main.fragment_categories.*
import moxy.MvpAppCompatFragment


class CategoriesFragment : MvpAppCompatFragment(), CategoriesMapView {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        val categories = initCategories()
        categoriesRv?.layoutManager = LinearLayoutManager(context)
        categoriesRv?.adapter = CategoryMapAdapter(object : CategoryMapListener {
            override fun clickChat(categoryName: String) {

            }
        }, categories)
    }

    private fun initCategories(): ArrayList<Category> {
        val categoryOne = Category(R.drawable.ic_star, "Тусовки")
        val categoryTwo = Category(R.drawable.ic_case, "Бизнес ивенты")
        val categoryThree = Category(R.drawable.ic_heart, "Кружок по интересам")
        val categoryFour = Category(R.drawable.ic_culture_category, "Культурные мероприятия")

        val categories = arrayListOf<Category>()
        categories.add(categoryOne)
        categories.add(categoryTwo)
        categories.add(categoryThree)
        categories.add(categoryFour)

        return categories
    }

}
