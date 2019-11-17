package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.R
import io.moonshard.moonshard.presentation.presenter.AddChatPresenter
import io.moonshard.moonshard.presentation.view.AddChatView
import kotlinx.android.synthetic.main.fragment_add_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class AddChatFragment : MvpAppCompatFragment(), AddChatView {
    override fun back() {
        val newFragment = ChatsFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container, newFragment)?.commit()
    }

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
        btn?.setOnClickListener {
            presenter.createGroupChat(editJid.text.toString())
        }

        btnOneToOne?.setOnClickListener {
            presenter.startChatWithPeer(editJudOneToOne.text.toString())
        }
    }

    override fun showError(text: String?) {
        text?.let {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
    }
}
