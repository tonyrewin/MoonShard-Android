package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import io.moonshard.moonshard.R
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.*
import io.moonshard.moonshard.ui.fragments.mychats.create.event.ChooseMapFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.CreateNewEventFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.TimeEventFragment


class MainChatFragment : Fragment() {

    var idChat: String = ""
    var fromMap: Boolean = false
    var stateChat: String = "join"
    var fromCreateNewChat: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideBottomNavigationBar()

        arguments?.let {
            idChat = it.getString("chatId")
            fromMap = it.getBoolean("fromMap", false)
            stateChat = it.getString("stateChat", "join")
            fromCreateNewChat = it.getBoolean("fromCreateNewChat", false)
        }
        showChatFirstStart()
    }

    private fun showChatFirstStart(jidChat: String = idChat) {
        val bundle = Bundle()
        bundle.putString("chatId", jidChat)
        bundle.putBoolean("fromMap", fromMap)
        bundle.putString("stateChat", stateChat)
        bundle.putBoolean("fromCreateNewChat",fromCreateNewChat)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, chatFragment, "realChatFragment:$jidChat").commit()
    }

    fun showChatWithStack() {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        bundle.putBoolean("fromMap", fromMap)
        bundle.putString("stateChat", stateChat)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.replace(R.id.mainContainer, chatFragment, "realChatFragment")
            .addToBackStack("realChatFragment").commit()
    }

    fun showChatInfo(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val chatFragment = ChatInfoFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, chatFragment, "ChatInfoFragment")
            .addToBackStack("ChatInfoFragment")
            .commit()
    }

    fun showProfileUserScreen(jid: String) {
        val bundle = Bundle()
        bundle.putString("userJid", jid)
        bundle.putBoolean("fromChatFragment", true)
        val fragment = ProfileUserFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "ProfileUserFragment")
            .addToBackStack("ProfileUserFragment")
            .commit()
    }

    fun showEventInfo(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val eventFragment = EventInfoFragment()
        eventFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, eventFragment, "EventInfoFragment")
            .addToBackStack("EventInfoFragment")
            .commit()
    }

    fun showManageChatScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val manageChatFragment =
            ManageChatFragment()
        manageChatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, manageChatFragment, "manageChatFragment")
            .addToBackStack("manageChatFragment")
            .commit()
    }

    fun showInviteNewUserScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment =
            InviteUserFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "InviteUserFragment")
            .addToBackStack("InviteUserFragment")
            .commit()
    }

    fun moveAndClearPopBackStack() {
        childFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    fun showAdminsScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = AdminsFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "AdminsFragment").hide(this)
            .addToBackStack("AdminsFragment")
            .commit()
    }

    fun showMembersScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = MembersChatFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "MembersChatFragment")
            .addToBackStack("MembersChatFragment")
            .commit()
    }

    fun showAddAdminScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment =
            AddAdminFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()

        ft.replace(R.id.mainContainer, fragment, "AddAdminFragment")
            .addToBackStack("AddAdminFragment")
            .commit()
    }

    fun showCreateNewEventScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        bundle.putBoolean("fromEventsFragment", true)
        val chatFragment = CreateNewEventFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, chatFragment, "CreateNewEventFragment")
            .addToBackStack("CreateNewEventFragment")
            .commit()
    }

    fun showTimeEventScreen() {
        val chatFragment =
            TimeEventFragment()
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.mainContainer, chatFragment, "TimeEventFragment")
            ?.addToBackStack("TimeEventFragment")?.commit()
    }

    fun showChooseMapScreen() {
        val fragment =
            ChooseMapFragment()
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "ChooseMapFragment")
            .addToBackStack("ChooseMapFragment").commit()
    }

    fun showChatScreen(jid: String) {
        val bundle = Bundle()
        bundle.putString("chatId", jid)
        bundle.putBoolean("fromEvent", true)
        val mainChatFragment = MainChatFragment()
        mainChatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, mainChatFragment, "chatScreen").addToBackStack("chatScreen")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }

        if (fromMap) (activity as? MainActivity)?.showBottomNavigationBar()
    }

    fun back() {
        fragmentManager?.popBackStack()
    }
}
