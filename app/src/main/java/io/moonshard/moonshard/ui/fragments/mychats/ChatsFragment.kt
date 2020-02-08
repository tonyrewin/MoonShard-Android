package io.moonshard.moonshard.ui.fragments.mychats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.models.ChatListItem
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.presentation.presenter.ChatsPresenter
import io.moonshard.moonshard.presentation.view.ChatsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.moonshard.moonshard.ui.adapters.ChatListListener
import kotlinx.android.synthetic.main.fragment_chats.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ChatsFragment : MvpAppCompatFragment(), ChatsView {
    override fun addNewChat(chat: GenericDialog) {

    }

    override fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    @InjectPresenter
    lateinit var presenter: ChatsPresenter
    private lateinit var chatListAdapter: ChatListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigationBar()

        chatsRv?.layoutManager = LinearLayoutManager(view.context)
        chatListAdapter = ChatListAdapter(this.mvpDelegate, object : ChatListListener {
            override fun clickChat(chat: ChatListItem) {
                showChatScreen(chat.jid.asUnescapedString())
            }
        })
        chatsRv?.adapter = chatListAdapter
        ItemTouchHelper((chatsRv.adapter as ChatListAdapter).SwipeToDeleteCallback()).attachToRecyclerView(
            chatsRv
        )

        presenter.observeChatList()
    }

    override fun updateChatList(chats: List<ChatListItem>) {
        chatListAdapter.setData(chats)
    }

    override fun showChatScreen(chatId: String) {
        (activity as? MainActivity)?.showMainChatScreen(chatId = chatId)
    }

    fun setFilter(text: String) {
        (chatsRv?.adapter as? ChatListAdapter)?.presenter?.setFilter(text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).hideBottomNavigationBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
}
