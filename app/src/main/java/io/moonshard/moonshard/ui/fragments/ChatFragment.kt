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
import io.moonshard.moonshard.ui.activities.RecyclerScrollMoreListener
import io.moonshard.moonshard.ui.adapters.MessagesAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ChatFragment : MvpAppCompatFragment(), ChatView {

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    override fun addToStart(message: GenericMessage, reverse: Boolean) {
        runOnUiThread {
            (messagesRv?.adapter as MessagesAdapter).addToStart(message, reverse)
        }
    }

    override fun addToEnd(msgs: ArrayList<GenericMessage>, reverse: Boolean) {
        runOnUiThread {
            (messagesRv.adapter as MessagesAdapter).addToEnd(msgs, reverse)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            io.moonshard.moonshard.R.layout.fragment_chat,
            container, false
        )
    }

    override fun cleanMessage() {
        editText?.text?.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  messagesRv?.layoutManager = LinearLayoutManager(view.context)
        //messagesRv?.adapter = MessagesAdapter(arrayListOf(), messagesRv.layoutManager as LinearLayoutManager)


        setAdapter()

        arguments?.let {
            val idChat = it.getString("chatId")
            presenter.setChatId(idChat)

            if (idChat.contains("conference")) presenter.join()

            presenter.loadLocalMessages()

            presenter.loadMoreMessages()
            sendMessage.setOnClickListener {
                presenter.sendMessage(editText?.text.toString())
            }
        }

       (messagesRv.adapter as MessagesAdapter).setLoadMoreListener(object :
           MessagesAdapter.OnLoadMoreListener{
           override fun onLoadMore(page: Int, totalItemsCount: Int) {
               presenter.loadRecentPageMessages()
           }
       })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadRecentPageMessages()
    }

    internal fun setAdapter() {
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, true
        )

        messagesRv?.layoutManager = layoutManager

        messagesRv?.adapter =
            MessagesAdapter(arrayListOf(), messagesRv.layoutManager as LinearLayoutManager)

        val listener = RecyclerScrollMoreListener(layoutManager,messagesRv.adapter as MessagesAdapter)

        messagesRv?.addOnScrollListener(listener)
    }
}
