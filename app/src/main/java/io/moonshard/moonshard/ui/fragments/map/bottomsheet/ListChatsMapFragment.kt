package io.moonshard.moonshard.ui.fragments.map.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.ListChatMapPresenter
import io.moonshard.moonshard.presentation.view.ListChatMapView
import io.moonshard.moonshard.ui.adapters.ListChatMapAdapter
import io.moonshard.moonshard.ui.adapters.ListChatMapListener
import kotlinx.android.synthetic.main.fragment_list_chats_map.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ListChatsMapFragment : MvpAppCompatFragment(), ListChatMapView {

    @InjectPresenter
    lateinit var presenter: ListChatMapPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_chats_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        presenter.getChats()
    }

    override fun setChats(chats: ArrayList<RoomPin>) {
        (groupsRv?.adapter as ListChatMapAdapter).setChats(chats)
    }

    private fun initAdapter() {
        //val categories = initCategories()
        // val rooms = RoomsMap.rooms
        groupsRv?.layoutManager = LinearLayoutManager(context)
        groupsRv?.adapter = ListChatMapAdapter(object : ListChatMapListener {
            override fun clickChat(room: RoomPin) {

            }
        }, arrayListOf())
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
