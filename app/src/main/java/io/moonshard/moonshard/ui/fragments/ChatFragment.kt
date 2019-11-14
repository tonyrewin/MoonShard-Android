package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.moonshard.moonshard.presentation.presenter.ChatPresenter
import io.moonshard.moonshard.presentation.view.ChatView
import io.moonshard.moonshard.services.NewMessageListener
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid


class ChatFragment : MvpAppCompatFragment(), ChatView {

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(io.moonshard.moonshard.R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            var idChat = it.getString("idChat")
        }

        sendMessage.setOnClickListener {
            presenter.sendMessage(editText?.text.toString())
        }
    }

}
