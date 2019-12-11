package io.moonshard.moonshard.ui.fragments.mychats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.ChatsPresenter
import io.moonshard.moonshard.presentation.view.ChatsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.moonshard.moonshard.ui.adapters.ChatListListener
import io.moonshard.moonshard.ui.fragments.mychats.chat.ChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.MessagesFragment
import kotlinx.android.synthetic.main.fragment_chats.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ChatsFragment : MvpAppCompatFragment(), ChatsView {

    override fun addNewChat(chat: GenericDialog) {

    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    @InjectPresenter
    lateinit var presenter: ChatsPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigationBar()

        chatsRv?.layoutManager = LinearLayoutManager(view.context)
        chatsRv?.adapter = ChatListAdapter(this.mvpDelegate, object : ChatListListener{
            override fun clickChat(chat: ChatEntity) {
                showChatScreen(chat.jid)
            }
        })
        ItemTouchHelper((chatsRv.adapter as ChatListAdapter).SwipeToDeleteCallback()).attachToRecyclerView(chatsRv)

        presenter.setDialogs()


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
