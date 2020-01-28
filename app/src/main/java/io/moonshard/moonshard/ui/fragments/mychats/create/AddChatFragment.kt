package io.moonshard.moonshard.ui.fragments.mychats.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.presentation.presenter.create_group.AddChatPresenter
import io.moonshard.moonshard.presentation.view.AddChatView
import io.moonshard.moonshard.ui.fragments.mychats.ChatsFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.chat.CreateNewChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.CreateNewEventFragment
import kotlinx.android.synthetic.main.fragment_add_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class AddChatFragment : MvpAppCompatFragment(), AddChatView {

    @InjectPresenter
    lateinit var presenter: AddChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLocalGroup?.setSafeOnClickListener {
            showCreateNewChatScreen()
        }
    }

    override fun showError(text: String?) {
        text?.let {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
    }

    override fun back() {
        val newFragment = ChatsFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, newFragment)?.commit()
    }

   override fun showCreateNewChatScreen(){
        val chatFragment =
            CreateNewChatFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "CreateNewEventFragment")?.
            addToBackStack("CreateNewEventFragment")?.commit()
    }
}
