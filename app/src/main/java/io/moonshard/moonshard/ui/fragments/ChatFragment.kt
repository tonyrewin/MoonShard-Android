package io.moonshard.moonshard.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.presentation.presenter.ChatPresenter
import io.moonshard.moonshard.presentation.view.ChatView
import io.moonshard.moonshard.ui.adapters.MessagesAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ChatFragment : MvpAppCompatFragment(), ChatView {

    override fun addMessage(message: GenericMessage) {

        runOnUiThread {
            (messages.adapter as MessagesAdapter).add(message)
            messages.scrollToPosition((messages.adapter as MessagesAdapter).itemCount - 1)
        }
    }

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

        messages?.layoutManager = LinearLayoutManager(view.context)
        messages?.adapter = MessagesAdapter(arrayListOf())

        arguments?.let {
            val idChat = it.getString("chatId")
            presenter.setChatId(idChat)
        }

        sendMessage.setOnClickListener {
             presenter.sendMessage(editText?.text.toString())
        }
    }

}
