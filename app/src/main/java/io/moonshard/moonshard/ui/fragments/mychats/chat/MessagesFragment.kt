package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.StreamUtil
import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.models.GenericMessage
import io.moonshard.moonshard.presentation.presenter.chat.MessagesPresenter
import io.moonshard.moonshard.presentation.view.MessagesView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.activities.RecyclerScrollMoreListener
import io.moonshard.moonshard.ui.adapters.chat.MessagesAdapter
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.ChatInfoFragment
import kotlinx.android.synthetic.main.messages_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class MessagesFragment : MvpAppCompatFragment(), MessagesView {

    @InjectPresenter
    lateinit var presenter: MessagesPresenter

    var idChat: String = ""

    override fun addToStart(message: GenericMessage, reverse: Boolean) {
        MainApplication.getMainUIThread().post {
            (messagesRv?.adapter as MessagesAdapter).addToStart(message, reverse)
        }
    }

    override fun addToEnd(msgs: ArrayList<GenericMessage>, reverse: Boolean) {
        MainApplication.getMainUIThread().post {
            (messagesRv?.adapter as MessagesAdapter).addToEnd(msgs, reverse)
        }
    }

    override fun setMessages(msgs: ArrayList<GenericMessage>, reverse: Boolean) {
        MainApplication.getMainUIThread().post {
            (messagesRv?.adapter as? MessagesAdapter)?.setMessages(msgs, reverse)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.messages_chat,
            container, false
        )
    }

    override fun cleanMessage() {
        editText?.text?.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.hideBottomNavigationBar()

        setAdapter()

        if(ChatRepository.idChatCurrent!=null){
            idChat = ChatRepository.idChatCurrent!!
        }else{
            arguments?.let {
                idChat = it.getString("chatId")
            }
        }
        presenter.setChatId(idChat)
        if (idChat.contains("conference")) presenter.join()

        sendMessage.setOnClickListener {
            presenter.sendMessage(editText.text.toString())
        }

        addAttachment?.setOnClickListener {
            chooseFile()
        }

        (messagesRv?.adapter as? MessagesAdapter)?.setLoadMoreListener(object :
            MessagesAdapter.OnLoadMoreListener {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                presenter.loadRecentPageMessages()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
        (activity as MainActivity).showBottomNavigationBar()

        for(i in fragmentManager!!.fragments.indices){
            if(fragmentManager!!.fragments[i].tag == "MapScreen"){
            //    (fragmentManager!!.fragments[i] as? MapFragment)?.showBottomSheet()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadRecentPageMessages()
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, true
        )

        messagesRv?.layoutManager = layoutManager

        messagesRv?.adapter =
            MessagesAdapter(
                arrayListOf(),
                messagesRv.layoutManager as LinearLayoutManager
            )

        val listener =
            RecyclerScrollMoreListener(layoutManager, messagesRv.adapter as MessagesAdapter)

        messagesRv?.addOnScrollListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data?.data

        if (uri != null) {
            val input = context?.contentResolver?.openInputStream(uri)
            if(input!=null){
                val file = StreamUtil.stream2file(input)
                presenter.sendFile(file)
            }
        }
    }

    private fun chooseFile() {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(chooseFile, 1)
    }

    private fun showChatInfo(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val chatFragment = ChatInfoFragment()
        chatFragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, chatFragment, "ChatInfoFragment")?.hide(this)
            ?.addToBackStack("ChatInfoFragment")
            ?.commit()
    }
}
