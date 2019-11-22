package io.moonshard.moonshard.ui.fragments.create_group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.AddChatPresenter
import io.moonshard.moonshard.presentation.view.AddChatView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.ChatsFragment
import kotlinx.android.synthetic.main.fragment_add_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class AddChatFragment : MvpAppCompatFragment(), AddChatView {

    @InjectPresenter
    lateinit var presenter: AddChatPresenter

    override fun back() {
        val newFragment = ChatsFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, newFragment)?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back?.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        (activity as MainActivity).hideBottomNavigationBar()
        btn?.setOnClickListener {
            presenter.createGroupChat(editJid.text.toString())
        }

        btnOneToOne?.setOnClickListener {
            presenter.startChatWithPeer(editJudOneToOne.text.toString())
        }

        startLocalGroup?.setOnClickListener {
            showCreateNewChatScreen()
        }
    }

    override fun showError(text: String?) {
        text?.let {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
    }

   override fun showCreateNewChatScreen(){
        val chatFragment = CreateNewChatFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, chatFragment, "CreateNewChatFragment")?.addToBackStack("CreateNewChatFragment")
            ?.commit()
    }
}
