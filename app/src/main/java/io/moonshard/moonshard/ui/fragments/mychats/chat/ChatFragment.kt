package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.presentation.presenter.chat.ChatPresenter
import io.moonshard.moonshard.presentation.view.chat.ChatView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.chats.MyChatsPagerAdapter
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.ChatInfoFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.ProfileUserFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ChatFragment : MvpAppCompatFragment(), ChatView {

    var idChat: String = ""
    var fromMap:Boolean? = false

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            idChat = it.getString("chatId")
            fromMap= it.getBoolean("fromMap")
            presenter.setChatId(idChat)
            ChatRepository.idChatCurrent = idChat
            presenter.isEvent()
        }

        avatarChat?.setOnClickListener {
            if (idChat.contains("conference")) {
                showChatInfo(idChat)
            } else {
                showProfileUser(idChat)
            }
        }

        valueMembersChatTv?.setOnClickListener {
            if (idChat.contains("conference")) {
                showChatInfo(idChat)
            } else {
                showProfileUser(idChat)
            }
        }

        nameChatTv?.setOnClickListener {
            if (idChat.contains("conference")) {
                showChatInfo(idChat)
            } else {
                showProfileUser(idChat)
            }
        }

        backBtn?.setOnClickListener {
            fragmentManager?.popBackStack()
            ChatRepository.clean()
        }
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

    fun showProfileUser(jid: String) {
        val bundle = Bundle()
        bundle.putString("userJid", jid)
        val fragment = ProfileUserFragment()
        fragment.arguments = bundle
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.container, fragment, "ProfileUserFragment")?.hide(this)
            ?.addToBackStack("ProfileUserFragment")
            ?.commit()
    }

    override fun setDataMuc(
        name: String, valueOccupants: Int, valueOnlineMembers: Int
    ) {
        nameChatTv?.text = name
        valueMembersChatTv.text = "$valueOccupants участников, $valueOnlineMembers онлайн"
    }

    override fun setNameUser(name: String) {
        nameChatTv?.text = name
    }

    override fun setAvatar(avatar: Bitmap?) {
        MainApplication.getMainUIThread().post {
            avatarChat?.setImageBitmap(avatar)
        }
    }

    override fun initViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CHAT, MyChatsPagerAdapter.TabItem.EVENTS)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }

    override fun initViewPagerFromEvent() {
        tabLayout.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CHAT)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ChatRepository.clean()

        fromMap?.let {
            if(it){
                (activity as MainActivity).showBottomNavigationBar()
            }
        }
    }
}
