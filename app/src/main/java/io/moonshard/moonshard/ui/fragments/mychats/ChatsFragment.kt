package io.moonshard.moonshard.ui.fragments.mychats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.Utils.hideKeyboard
import io.moonshard.moonshard.models.ChatListItem
import io.moonshard.moonshard.models.GenericDialog
import io.moonshard.moonshard.models.dbEntities.ChatEntity
import io.moonshard.moonshard.presentation.presenter.ChatsPresenter
import io.moonshard.moonshard.presentation.view.ChatsView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.ChatListAdapter
import io.moonshard.moonshard.ui.adapters.ChatListListener
import io.moonshard.moonshard.ui.fragments.mychats.chat.ChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.CreateGroupFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_chats.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smackx.search.UserSearchManager


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
                showChatScreen(chat.jid.asUnescapedString(), chat.chatName)
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

    override fun showChatScreen(chatId: String, chatName: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        bundle.putSerializable("chatName", chatName)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "chatScreen")?.addToBackStack("chatScreen")
            ?.commit()
    }

    fun setFilter(text:String){
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
