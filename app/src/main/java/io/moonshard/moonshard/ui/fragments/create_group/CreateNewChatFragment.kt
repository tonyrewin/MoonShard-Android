package io.moonshard.moonshard.ui.fragments.create_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
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

        val categoryOne = Category(R.drawable.ic_star, "Тусовки")
        val categoryTwo = Category(R.drawable.ic_case, "Бизнес ивенты")
        val categoryThree = Category(R.drawable.ic_heart, "Кружок по интересам")
        val categoryFour = Category(R.drawable.ic_culture_category, "Культурные мероприятия")

        val categories = arrayListOf<Category>()
        categories.add(categoryOne)
        categories.add(categoryTwo)
        categories.add(categoryThree)
        categories.add(categoryFour)

        categoriesRv?.layoutManager = LinearLayoutManager(view.context)
        categoriesRv?.adapter = CategoriesAdapter(object : CategoryListener {
            override fun clickChat(categoryName: String) {
                ChooseChatRepository.category = categoryName
            }
        }, categories)


        if (ChooseChatRepository.address.isEmpty()) {
            address?.text = MainApplication.getAdress()
        } else {
            address?.text = ChooseChatRepository.address
        }

        if(ChooseChatRepository.time.isEmpty()){
            timeTv?.text = "3 hours"
        }else{
            timeTv?.text = ChooseChatRepository.time
        }

        timesLayout?.setOnClickListener {
            ChooseChatRepository.name = nameTv?.text.toString()
            showTimesScreen()
        }

        location?.setOnClickListener {
            val chatFragment = ChooseMapFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, chatFragment, "ChooseMapFragment")
                ?.addToBackStack("ChooseMapFragment")
                ?.commit()
        }

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    fun showTimesScreen() {
        val chatFragment = TimeGroupChatFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "TimeGroupChatFragment")
            ?.addToBackStack("TimeGroupChatFragment")
            ?.commit()
    }
}
