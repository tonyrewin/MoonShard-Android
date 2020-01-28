package io.moonshard.moonshard.ui.fragments.mychats.create.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.create_group.CreateNewChatPresenter
import io.moonshard.moonshard.presentation.view.create.CreateNewChatView
import io.moonshard.moonshard.ui.fragments.map.MapFragment
import io.moonshard.moonshard.ui.fragments.mychats.ChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.MyChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.ChatFragment
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

        newChat?.setSafeOnClickListener {
            presenter.createGroupChat(nameTv.text.toString())
        }

        back?.setSafeOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun showChatsScreen() {
        val fragment = MyChatsFragment()
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment, null)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun showChatScreen(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        bundle.putBoolean("fromCreateNewChat",true)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "CreatedChatScreen")?.commit()
    }

    override fun showToast(text: String) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }
}
