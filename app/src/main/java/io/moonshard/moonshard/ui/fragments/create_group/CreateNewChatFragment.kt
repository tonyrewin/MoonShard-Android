package io.moonshard.moonshard.ui.fragments.create_group

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication

import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.ui.adapters.CategoriesAdapter
import io.moonshard.moonshard.ui.adapters.CategoryListener
import kotlinx.android.synthetic.main.fragment_create_new_chat.*

class CreateNewChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_chat, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryOne = Category(R.drawable.ic_star,"Тусовки")
        val categoryTwo = Category(R.drawable.ic_case,"Бизнес ивенты")
        val categoryThree = Category(R.drawable.ic_heart,"Кружок по интересам")
        val categoryFour = Category(R.drawable.ic_culture_category,"Культурные мероприятия")

        val categories = arrayListOf<Category>()
        categories.add(categoryOne)
        categories.add(categoryTwo)
        categories.add(categoryThree)
        categories.add(categoryFour)

        categoriesRv?.layoutManager = LinearLayoutManager(view.context)
        categoriesRv?.adapter = CategoriesAdapter(object : CategoryListener {
            override fun clickChat(idChat: String) {
            }
        }, categories)

        address?.text = MainApplication.getAdress()

        timesLayout?.setOnClickListener {
            showTimesScreen()
        }

        location?.setOnClickListener {
            val chatFragment = ChooseMapFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, chatFragment, "ChooseMapFragment")?.addToBackStack("ChooseMapFragment")
                ?.commit()
        }
    }

    fun showTimesScreen(){
        val chatFragment = TimeGroupChatFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "TimeGroupChatFragment")?.addToBackStack("TimeGroupChatFragment")
            ?.commit()
    }
}
