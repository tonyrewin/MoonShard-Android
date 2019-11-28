package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.helpers.AvatarImageLoader
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.ChatsPresenter
import io.moonshard.moonshard.presentation.view.ChatsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.moonshard.moonshard.ui.adapters.ChatListListener

import io.moonshard.moonshard.ui.fragments.chat.ChatFragment
import io.moonshard.moonshard.ui.fragments.create_group.AddChatFragment
import kotlinx.android.synthetic.main.fragment_chats.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import java.util.*


class ChatsFragment : MvpAppCompatFragment(), ChatsView {

    override fun addNewChat(chat: GenericDialog) {

    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

   // override fun setData(chats: ArrayList<GenericDialog>) {
      //  (chatsRv.adapter as? MyChatsAdapter)?.setItems(chats)
   // }

    @InjectPresenter
    lateinit var presenter: ChatsPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigationBar()


        /*
        (activity as MainActivity).showBottomNavigationBar()
        chatsRv?.layoutManager = LinearLayoutManager(view.context)
        chatsRv?.adapter = MyChatsAdapter(object : RvListener {
            override fun clickChat(idChat: String) {
                showChatScreen(idChat)
                Log.d("chatsFragment", "was click on chat item")
            }
        }, arrayListOf(), AvatarImageLoader(this))

         */

        chatsRv?.layoutManager = LinearLayoutManager(view.context)
        chatsRv?.adapter = ChatListAdapter(this.mvpDelegate,object : ChatListListener{
            override fun clickChat(chat: ChatEntity) {
                showChatScreen(chat.jid)
            }
        })

        presenter.setDialogs()

        find?.setOnClickListener {
        }

        newChat?.setOnClickListener {
            (activity as MainActivity).hideBottomNavigationBar()
            val newFragment = AddChatFragment()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.container, newFragment,"AddChatFragment")?.addToBackStack("AddChatFragment")
                ?.commit()
        }
    }

    override fun showChatScreen(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "chatScreen")?.addToBackStack("chatScreen")
            ?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
}
