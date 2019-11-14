package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.helpers.ChatsHelper
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.presentation.presenter.ChatsPresenter
import io.moonshard.moonshard.presentation.view.ChatsView
import io.moonshard.moonshard.ui.adapters.MyChatsAdapter
import io.moonshard.moonshard.ui.adapters.RvListener
import kotlinx.android.synthetic.main.fragment_chats.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.ArrayList


class ChatsFragment : MvpAppCompatFragment(), ChatsView {

    override fun setData(chats:ArrayList<GenericDialog>) {
        chats.add(GenericDialog())
        (chatsRv.adapter as? MyChatsAdapter)?.setItems(chats)
        //(chatsRv as? MyChatsAdapter)?.sort(chats)
    }

    @InjectPresenter
    lateinit var presenter: ChatsPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsRv?.let {
            chatsRv.layoutManager = LinearLayoutManager(view.context)

            chatsRv.adapter = MyChatsAdapter(object : RvListener{
                override fun clickChat(idChat: String) {
                    showChatScreen(idChat)
                    Log.d("chatsFragment","was click on chat item")
                }
            }, arrayListOf())
        }
        presenter.setDialogs()
    }

    fun showChatScreen(chatId:String){
        val bundle = Bundle()
        bundle.putString("chatId",chatId)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
}
