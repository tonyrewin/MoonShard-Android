package io.moonshard.moonshard.ui.fragments.map.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.api.Category
import io.moonshard.moonshard.presentation.presenter.CategoriesMapPresenter
import io.moonshard.moonshard.presentation.view.CategoriesMapView
import io.moonshard.moonshard.ui.adapters.CategoryMapAdapter
import io.moonshard.moonshard.ui.adapters.CategoryMapListener
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import kotlinx.android.synthetic.main.fragment_categories.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class CategoriesFragment : MvpAppCompatFragment(), CategoriesMapView {

    @InjectPresenter
    lateinit var presenter: CategoriesMapPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        presenter.getCategories()
    }

    private fun initAdapter() {
        categoriesRv?.layoutManager = LinearLayoutManager(context)
        categoriesRv?.adapter = CategoryMapAdapter(object : CategoryMapListener {
            override fun clickChat(category: Category) {
                getRoomsById(category)
            }
        }, arrayListOf())
    }


    fun getRoomsById(category: Category) {
        for (i in fragmentManager!!.fragments.indices) {
            if (fragmentManager!!.fragments[i].tag == "MapScreen") {
                (fragmentManager!!.fragments[i] as? MapFragment)?.hideBottomSheet()
                (fragmentManager!!.fragments[i] as? MapFragment)?.update(category)
            }
        }
    }

    override fun showCategories(categories: ArrayList<Category>) {
        (categoriesRv?.adapter as CategoryMapAdapter).updateCategories(categories)
    }

}
