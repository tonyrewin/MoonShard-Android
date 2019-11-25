package io.moonshard.moonshard.ui.fragments.create_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChooseChatRepository
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewChatPresenter
import io.moonshard.moonshard.presentation.view.CreateNewChatView
import io.moonshard.moonshard.ui.adapters.CategoriesAdapter
import io.moonshard.moonshard.ui.adapters.CategoryListener
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import kotlinx.android.synthetic.main.fragment_create_new_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class CreateNewChatFragment : MvpAppCompatFragment(), CreateNewChatView {

    @InjectPresenter
    lateinit var presenter: CreateNewChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        if (ChooseChatRepository.address.isEmpty()) {
            address?.text = MainApplication.getAdress()
        } else {
            address?.text = ChooseChatRepository.address
        }

        if (ChooseChatRepository.time.isEmpty()) {
            timeTv?.text = "3 hours"
        } else {
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

        newChat?.setOnClickListener {
            presenter.createGroupChat(
                nameTv?.text.toString(),
                ChooseChatRepository.lat,
                ChooseChatRepository.lng,
                ChooseChatRepository.getTimeSec(),
                ChooseChatRepository.category
            )
        }

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    private fun initAdapter() {
        val categories = initCategories()
        categoriesRv?.layoutManager = LinearLayoutManager(context)
        categoriesRv?.adapter = CategoriesAdapter(object : CategoryListener {
            override fun clickChat(categoryName: String) {
                ChooseChatRepository.category = categoryName
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

    override fun showMapScreen() {
        val mapFragment = MapFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, mapFragment, null)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    private fun showTimesScreen() {
        val chatFragment = TimeGroupChatFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "TimeGroupChatFragment")
            ?.addToBackStack("TimeGroupChatFragment")
            ?.commit()
    }
}
