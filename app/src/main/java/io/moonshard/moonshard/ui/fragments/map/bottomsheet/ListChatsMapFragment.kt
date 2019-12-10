package io.moonshard.moonshard.ui.fragments.map.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.Category
import io.moonshard.moonshard.models.api.RoomPin
import io.moonshard.moonshard.presentation.presenter.ListChatMapPresenter
import io.moonshard.moonshard.presentation.view.ListChatMapView
import io.moonshard.moonshard.ui.adapters.ListChatMapAdapter
import io.moonshard.moonshard.ui.adapters.ListChatMapListener
import io.moonshard.moonshard.ui.fragments.chat.ChatFragment
import io.moonshard.moonshard.ui.fragments.map.MapFragment
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
        groupsRv?.layoutManager = LinearLayoutManager(context)
        groupsRv?.adapter = ListChatMapAdapter(object : ListChatMapListener {
            override fun clickChat(room: RoomPin) {
                presenter.joinChat(room.roomId.toString())
            }
        }, arrayListOf())
    }

    override fun showChatScreens(chatId: String) {
        var fragment: Fragment? = null
        MainApplication.getMainUIThread().post {
            /*
            for(i in fragmentManager!!.fragments.indices){
                if(fragmentManager!!.fragments[i].tag == "MapScreen"){
                    fragment = (fragmentManager!!.fragments[i] as? MapFragment)
                    (fragmentManager!!.fragments[i] as? MapFragment)?.collapsedBottomSheet()
                }
            }

             */
            val bundle = Bundle()
            bundle.putString("chatId", chatId)
            val chatFragment = ChatFragment()
            chatFragment.arguments = bundle
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.add(R.id.container, chatFragment)?.hide(this)?.hide(fragment!!)?.addToBackStack(null)
                ?.commit()
        }
    }

    fun updateChats(){
        presenter.getChats()
    }

}
