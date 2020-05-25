package io.moonshard.moonshard.ui.fragments.mychats.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import io.moonshard.moonshard.R
import io.moonshard.moonshard.db.ChangeEventRepository
import io.moonshard.moonshard.presentation.view.chat.MainChatView
import io.moonshard.moonshard.ui.activities.MainActivity
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.*
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.chat.ChatInfoFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.chat.ManageChatFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.event.EventInfoFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.event.ManageEventFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.event.admin_permissions.AdminPermissionFragment
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.*
import io.moonshard.moonshard.ui.fragments.mychats.chat.info.tickets.statics.*
import io.moonshard.moonshard.ui.fragments.mychats.create.event.ChooseMapFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.CreateNewEventFragment
import io.moonshard.moonshard.ui.fragments.mychats.create.event.TimeEventFragment
import moxy.MvpAppCompatFragment
import org.jivesoftware.smackx.muc.Occupant


class MainChatFragment : MvpAppCompatFragment(), MainChatView {

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
        bundle.putBoolean("fromCreateNewChat", fromCreateNewChat)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, chatFragment, "realChatFragment:$jidChat").commit()
    }

    fun showChatWithStack(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        bundle.putBoolean("fromMap", fromMap)
        bundle.putString("stateChat", stateChat)
        val chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, chatFragment, "realChatFragment:$chatId").commit()
    }

    fun showChatInfo(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val chatFragment =
            ChatInfoFragment()
        chatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, chatFragment, "ChatInfoFragment:$chatId")
            .addToBackStack("ChatInfoFragment:$chatId")
            .commit()
    }

    fun showProfileUserScreen(jid: String, fromMuc: Boolean = true) {
        val bundle = Bundle()
        bundle.putString("userJid", jid)
        bundle.putBoolean("fromMuc", fromMuc)
        val fragment = ProfileUserFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.add(R.id.mainContainer, fragment, "ProfileUserFragment$jid")
            .addToBackStack("ProfileUserFragment:$jid")
            .commit()
    }

    fun showEventInfo(chatId: String) {
        val bundle = Bundle()
        bundle.putString("chatId", chatId)
        val eventFragment =
            EventInfoFragment()
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

    fun showManageEventScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val manageChatFragment =
            ManageEventFragment()
        manageChatFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, manageChatFragment, "manageEventFragment")
            .addToBackStack("manageEventFragment")
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

    fun moveAndClearPopBackStackChild() {
        childFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    fun moveAndClearPopBackStack() {
        fragmentManager?.popBackStack(
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

    fun showManageTicketsScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = TicketsManageFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "TicketsManageFragment")
            .addToBackStack("TicketsManageFragment")
            .commit()
    }

    fun showManageTypesTicketScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = ManageTypesTicketFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "ManageTypesTicketFragment")
            .addToBackStack("ManageTypesTicketFragment")
            .commit()
    }


    fun showAddNewTypeTicketScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = AddNewTypeTicketFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "AddNewTypeTicketFragment")
            .addToBackStack("AddNewTypeTicketFragment")
            .commit()
    }

    fun showBuyTicketsScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = BuyTicketsFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "BuyTicketsFragment")
            .addToBackStack("BuyTicketsFragment")
            .commit()
    }

    fun showConfirmBuyTicketsFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = ConfirmBuyTicketsFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "ConfirmBuyTicketsFragment")
            .addToBackStack("ConfirmBuyTicketsFragment")
            .commit()
    }

    fun showScanQrTicketFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = ScanQrTicketFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "ScanQrTicketFragment")
            .addToBackStack("ScanQrTicketFragment")
            .commit()
    }

    fun showStatisticTicketsFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = StatisticTicketsFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "StatisticTicketsFragment")
            .addToBackStack("StatisticTicketsFragment")
            .commit()
    }

    fun showNotUsedTicketsFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = NotUsedTicketsFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "NotUsedTicketsFragment")
            .addToBackStack("NotUsedTicketsFragment")
            .commit()
    }

    fun showSalesTicketFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = SalesTicketFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "SalesTicketFragment")
            .addToBackStack("SalesTicketFragment")
            .commit()
    }

    fun showScannedTicketFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = ScannedTicketFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "ScannedTicketFragment")
            .addToBackStack("ScannedTicketFragment")
            .commit()
    }


    fun showSearchTicketFragment(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment =
            SearchTicketFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "SearchTicketFragment")
            .addToBackStack("SearchTicketFragment")
            .commit()
    }

    fun showAddAdminScreen(idChat: String) {
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        val fragment = AddAdminFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()

        ft.replace(R.id.mainContainer, fragment, "AddAdminFragment")
            .addToBackStack("AddAdminFragment")
            .commit()
    }

    fun showAdminPermissionFragment(
        idChat: String,
        occupant: Occupant
    ){
        val bundle = Bundle()
        bundle.putString("chatId", idChat)
        bundle.putString("occupant", Gson().toJson(occupant));
        val fragment = AdminPermissionFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()

        ft.replace(R.id.mainContainer, fragment, "AdminPermissionFragment")
            .addToBackStack("AdminPermissionFragment")
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

    fun showTimeEventScreen(fromManageEventScreen: Boolean = false) {
        val bundle = Bundle()
        bundle.putBoolean("fromManageEventScreen", fromManageEventScreen)
        val fragment =
            TimeEventFragment()
        fragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment, "TimeEventFragment")
            .addToBackStack("TimeEventFragment").commit()
    }

    fun showChooseMapScreen(fromManageEventScreen: Boolean = false) {
        val bundle = Bundle()
        bundle.putBoolean("fromManageEventScreen", fromManageEventScreen)
        val fragment =
            ChooseMapFragment()
        fragment.arguments = bundle
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


    fun showFullScreen(url: String) {
        val bundle = Bundle()
        bundle.putString("url", url)
        val eventFragment = FullPhotoFragment()
        eventFragment.arguments = bundle
        val ft = childFragmentManager.beginTransaction()
        ft.add(R.id.mainContainer, eventFragment).addToBackStack("FullPhotoFragment").commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }

        if (fromMap) (activity as? MainActivity)?.showBottomNavigationBar()
        ChangeEventRepository.clean()
    }
}
