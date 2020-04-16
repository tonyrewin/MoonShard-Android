package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import io.moonshard.moonshard.MainApplication
import io.moonshard.moonshard.R
import io.moonshard.moonshard.common.utils.setSafeOnClickListener
import io.moonshard.moonshard.db.ChatRepository
import io.moonshard.moonshard.presentation.presenter.chat.ChatPresenter
import io.moonshard.moonshard.presentation.view.chat.ChatView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.adapters.chats.MyChatsPagerAdapter
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.FullPhotoFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter


class ChatFragment : MvpAppCompatFragment(), ChatView {

    var idChat: String = ""
    var fromMap: Boolean? = false
    var fromCreateNewChat: Boolean = false
    var stateChat: String = "join"

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
            fromMap = it.getBoolean("fromMap", false)
            stateChat = it.getString("stateChat", "join")
            fromCreateNewChat = it.getBoolean("fromCreateNewChat", false)
            presenter.setChatId(idChat)
            ChatRepository.idChatCurrent = idChat
            ChatRepository.stateChat = stateChat
            presenter.isEvent()
        }

        backBtn?.setSafeOnClickListener {
            if (fromCreateNewChat) {
                activity!!.supportFragmentManager.popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            } else {
                if(parentFragment is MainChatFragment){
                    if(parentFragment!!.childFragmentManager.backStackEntryCount==0){
                        parentFragment!!.fragmentManager?.popBackStack()
                    }else{
                        parentFragment!!.childFragmentManager.popBackStackImmediate()
                    }
                }
            }
            ChatRepository.clean()
        }
    }

    private fun initToolBarInfoChat() {
        avatarChat?.setSafeOnClickListener {
            showChatInfo(idChat)
        }

        valueMembersChatTv?.setSafeOnClickListener {
            showChatInfo(idChat)
        }

        nameChatTv?.setSafeOnClickListener {
            showChatInfo(idChat)
        }
    }

    private fun initToolBarInfoEvent() {
        avatarChat?.setSafeOnClickListener {
            if (idChat.contains("conference")) {
                showEventInfo(idChat)
            } else {
                showProfileUser(idChat)
            }
        }

        valueMembersChatTv?.setSafeOnClickListener {
            if (idChat.contains("conference")) {
                showEventInfo(idChat)
            } else {
                showProfileUser(idChat)
            }
        }

        nameChatTv?.setSafeOnClickListener {
            if (idChat.contains("conference")) {
                showEventInfo(idChat)
            } else {
                showProfileUser(idChat)
            }
        }
    }

    private fun showEventInfo(chatId: String) {
        (parentFragment as? MainChatFragment)?.showEventInfo(chatId)
    }

    fun showCreateNewEventScreen(idChat: String) {
        (parentFragment as? MainChatFragment)?.showCreateNewEventScreen(idChat)
    }

    fun showChatFragment(jid:String){
        (parentFragment as? MainChatFragment)?.showChatScreen(jid)
    }

    private fun showChatInfo(chatId: String) {
        (parentFragment as? MainChatFragment)?.showChatInfo(chatId)
    }

     fun showProfileUser(jid: String) {
        (parentFragment as? MainChatFragment)?.showProfileUserScreen(jid,false)
    }

    fun showProfileUserFromMuc(jid: String){
        val jidUser = presenter.getFullStringUser(jid)
        (parentFragment as? MainChatFragment)?.showProfileUserScreen(jidUser,true)
    }

    fun showFullScreen(url:String){
        //toolBar?.visibility = View.GONE
        (parentFragment as? MainChatFragment)?.showFullScreen(url)
    }

    override fun setDataMuc(
        name: String, valueOccupants: Int, valueOnlineMembers: Int
    ) {
        nameChatTv?.text = name
        valueMembersChatTv.text = "$valueOccupants " + getString(R.string.members) + ", $valueOnlineMembers " + getString(R.string.online)
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
        initToolBarInfoChat()
        tabLayout?.setupWithViewPager(viewPager)
        val sectionsPagerAdapter = MyChatsPagerAdapter(
            childFragmentManager,
            context!!,
            arrayOf(MyChatsPagerAdapter.TabItem.CHAT, MyChatsPagerAdapter.TabItem.EVENTS)
        )
        viewPager?.adapter = sectionsPagerAdapter
    }

    override fun initViewPagerFromEvent() {
        tabLayout?.visibility = View.GONE
        initToolBarInfoEvent()

        tabLayout?.setupWithViewPager(viewPager)
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


/*
        for (fragment in childFragmentManager.fragments) {
        childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
 */
        ChatRepository.clean()
    }

}
