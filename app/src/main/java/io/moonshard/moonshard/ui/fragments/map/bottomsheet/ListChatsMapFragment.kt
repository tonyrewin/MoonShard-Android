package io.moonshard.moonshard.ui.fragments.map.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.ListChatMapPresenter
import io.moonshard.moonshard.presentation.view.ListChatMapView
import io.moonshard.moonshard.ui.adapters.ListChatMapAdapter
import io.moonshard.moonshard.ui.adapters.ListChatMapListener
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MainChatFragment
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
        return inflater.inflate(R.layout.fragment_list_chats_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        presenter.getChats()
    }

    override fun setChats(chats: ArrayList<RoomPin>) {
        (groupsRv?.adapter as? ListChatMapAdapter)?.setChats(chats)
    }

    private fun initAdapter() {
        groupsRv?.layoutManager = LinearLayoutManager(context)
        groupsRv?.adapter = ListChatMapAdapter(object : ListChatMapListener {
            override fun clickChat(room: RoomPin) {
                (parentFragment as? MapFragment)?.showMarkerBottomSheet(room)
            }
        }, arrayListOf())
    }

    fun updateChats(){
        presenter.getChats()
    }

    fun setFilter(text: String) {
        presenter.setFilter(text)
    }

    override fun onDataChange(){
        (groupsRv?.adapter as? ListChatMapAdapter)?.notifyDataSetChanged()
    }

    override fun updatePinsOnMap( events:ArrayList<RoomPin>) {
        (parentFragment as MapFragment).updateRoomsLocale(events)
    }
}
